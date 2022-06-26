package com.lzhpo.panda.gateway.filter;

import com.lzhpo.panda.gateway.RouteDefinitionLocator;
import com.lzhpo.panda.gateway.core.ComponentDefinition;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.consts.GatewayConst;
import com.lzhpo.panda.gateway.predicate.factory.RoutePredicateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class WebRequestFilter extends OncePerRequestFilter implements Ordered {

  private final RestTemplate restTemplate;
  private final RouteDefinitionLocator routeDefinitionLocator;

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

    List<GlobalFilterAdapter> globalFilters = routeDefinitionLocator.getGlobalFilterAdapters();
    List<RouteFilter> filters = new ArrayList<>(globalFilters);
    RouteDefinition route = lookupRoute(request);

    if (!ObjectUtils.isEmpty(route)) {
      request.setAttribute(GatewayConst.ROUTE_DEFINITION, route);
      List<ComponentDefinition> filterDefinitions = route.getFilters();

      List<RouteFilter> routeFilters =
          filterDefinitions.stream()
              .map(
                  filterDefinition ->
                      routeDefinitionLocator
                          .getFilterFactory(filterDefinition.getName())
                          .create(filterDefinition))
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

      filters.addAll(routeFilters);
      filters.add(new ForwardRouteFilter(restTemplate));
    }

    AnnotationAwareOrderComparator.sort(filters);
    DefaultRouteFilterChain.create(filters).doFilter(request, response);
  }

  /**
   * Execute route predicate, in order to find match route.
   *
   * @param request {@link HttpServletRequest}
   * @return matched route
   */
  private RouteDefinition lookupRoute(HttpServletRequest request) {
    List<RouteDefinition> routes = routeDefinitionLocator.getRoutes();
    return routes.stream()
        .peek(
            routeDefinition -> request.setAttribute(GatewayConst.ROUTE_DEFINITION, routeDefinition))
        .filter(
            route ->
                route.getPredicates().stream()
                    .filter(
                        predicateDefinition -> {
                          String predicateName = predicateDefinition.getName();
                          RoutePredicateFactory<Object> predicateFactory =
                              routeDefinitionLocator.getPredicateFactory(predicateName);
                          if (Objects.isNull(predicateFactory)) {
                            log.error("Not found [{}] predicateFactory.", predicateName);
                            return false;
                          }
                          return true;
                        })
                    .map(
                        predicateDefinition ->
                            routeDefinitionLocator
                                .getPredicateFactory(predicateDefinition.getName())
                                .create(predicateDefinition)
                                .test(request))
                    .filter(Boolean.TRUE::equals)
                    .findAny()
                    .orElse(false))
        .findFirst()
        .orElse(null);
  }
}
