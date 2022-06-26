package com.lzhpo.panda.gateway.servlet.filter.support;

import com.lzhpo.panda.gateway.core.ComponentDefinition;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.consts.GatewayConst;
import com.lzhpo.panda.gateway.servlet.RouteComponentLocator;
import com.lzhpo.panda.gateway.servlet.RouteDefinitionLocator;
import com.lzhpo.panda.gateway.servlet.filter.RouteFilter;
import com.lzhpo.panda.gateway.servlet.filter.chain.DefaultRouteFilterChain;
import com.lzhpo.panda.gateway.servlet.filter.global.GlobalFilterAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class WebRequestFilter extends OncePerRequestFilter implements Ordered {

  private final RestTemplate restTemplate;
  private final RouteDefinitionLocator routeDefinitionLocator;
  private final RouteComponentLocator routeComponentLocator;

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

    List<GlobalFilterAdapter> globalFilters = routeComponentLocator.getGlobalFilterAdapters();
    List<RouteFilter> filters = new ArrayList<>(globalFilters);
    RouteDefinition route = lookupRoute(request);

    if (!ObjectUtils.isEmpty(route)) {
      request.setAttribute(GatewayConst.ROUTE_DEFINITION, route);
      List<ComponentDefinition> filterDefinitions = route.getFilters();

      List<RouteFilter> routeFilters =
          filterDefinitions.stream()
              .map(filter -> routeComponentLocator.getFilterFactory(filter.getName()).apply(filter))
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

      filters.addAll(routeFilters);
      filters.add(new ForwardRouteFilter(restTemplate));
    }

    AnnotationAwareOrderComparator.sort(filters);
    DefaultRouteFilterChain.create(filters).doFilter(request, response);
  }

  private RouteDefinition lookupRoute(HttpServletRequest request) {
    List<RouteDefinition> routes = routeDefinitionLocator.getRoutes();
    return routes.stream()
        .filter(
            route ->
                route.getPredicates().stream()
                    .map(
                        predicate ->
                            routeComponentLocator
                                .getPredicateFactory(predicate.getName())
                                .apply(predicate)
                                .test(request))
                    .filter(Boolean.TRUE::equals)
                    .findAny()
                    .orElse(false))
        .findAny()
        .orElse(null);
  }
}
