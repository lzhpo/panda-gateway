package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.core.exception.GatewayCustomException;
import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RelationType;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.filter.DefaultRouteFilterChain;
import com.lzhpo.panda.gateway.filter.ForwardRouteFilter;
import com.lzhpo.panda.gateway.filter.GlobalFilterAdapter;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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
public class GatewayRequestMapping extends OncePerRequestFilter implements Ordered {

  private final RestTemplate restTemplate;
  private final RouteDefinitionLocator routeDefinitionLocator;

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  @Override
  protected void doFilterInternal(
      @Nonnull HttpServletRequest request,
      @Nonnull HttpServletResponse response,
      @Nonnull FilterChain filterChain)
      throws ServletException, IOException {

    List<GlobalFilterAdapter> globalFilters = routeDefinitionLocator.getGlobalFilterAdapters();
    List<RouteFilter> filters = new ArrayList<>(globalFilters);
    RouteDefinition route = lookupRoute(request);

    if (!ObjectUtils.isEmpty(route)) {
      List<ComponentDefinition> filterDefinitions = route.getFilters();
      List<RouteFilter> routeFilters =
          filterDefinitions.stream()
              .map(
                  filterDefinition -> {
                    String filterName = filterDefinition.getName();
                    return Optional.ofNullable(routeDefinitionLocator.getFilterFactory(filterName))
                        .map(filterFactory -> filterFactory.create(filterDefinition))
                        .orElseGet(
                            () -> {
                              log.error("Not found [{}] filterFactory.", filterName);
                              return null;
                            });
                  })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

      filters.addAll(routeFilters);
      filters.add(new ForwardRouteFilter(restTemplate));
    }

    AnnotationAwareOrderComparator.sort(filters);
    DefaultRouteFilterChain.create(filters).doFilter(request, response);

    if (!response.isCommitted()) {
      filterChain.doFilter(request, response);
    }
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
        .filter(
            route -> {
              request.setAttribute(GatewayConst.ROUTE_DEFINITION, route);
              RelationType relationType = route.getEnhances().getPredicatesRelation();
              switch (relationType) {
                case AND:
                  return route.getPredicates().stream()
                      .allMatch(predicateDefinition -> testPredicate(request, predicateDefinition));
                case OR:
                  return route.getPredicates().stream()
                      .anyMatch(predicateDefinition -> testPredicate(request, predicateDefinition));
                default:
                  throw new GatewayCustomException("Not support relation type " + relationType);
              }
            })
        .findFirst()
        .orElse(null);
  }

  /**
   * Execute predicate
   *
   * @param request {@link HttpServletRequest}
   * @param predicateDefinition {@link ComponentDefinition}
   * @return test predicate result
   */
  private Boolean testPredicate(
      HttpServletRequest request, ComponentDefinition predicateDefinition) {
    String predicateName = predicateDefinition.getName();
    return Optional.ofNullable(routeDefinitionLocator.getPredicateFactory(predicateName))
        .map(predicateFactory -> predicateFactory.create(predicateDefinition).test(request))
        .orElseGet(
            () -> {
              log.error("Not found [{}] predicateFactory.", predicateName);
              return false;
            });
  }
}
