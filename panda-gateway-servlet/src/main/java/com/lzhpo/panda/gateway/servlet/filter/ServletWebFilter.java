package com.lzhpo.panda.gateway.servlet.filter;

import com.lzhpo.panda.gateway.core.FilterDefinition;
import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.PredicateDefinition;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.consts.GatewayConst;
import com.lzhpo.panda.gateway.servlet.RouteComponentLocator;
import com.lzhpo.panda.gateway.servlet.filter.support.GlobalServletFilterAdapter;
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
public class ServletWebFilter extends OncePerRequestFilter implements Ordered {

  private final RestTemplate restTemplate;
  private final GatewayProperties gatewayProperties;
  private final RouteComponentLocator routeComponentLocator;

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

    List<GlobalServletFilterAdapter> globalFilters = routeComponentLocator.getGlobalFilters();
    List<ServletFilter> filters = new ArrayList<>(globalFilters);
    RouteDefinition route = lookupRoute(request);

    if (!ObjectUtils.isEmpty(route)) {
      request.setAttribute(GatewayConst.ROUTE_DEFINITION, route);
      List<ServletFilter> routeFilters =
          route.getFilters().stream()
              .map(FilterDefinition::getName)
              .map(routeComponentLocator::getFilter)
              .collect(Collectors.toList());
      filters.addAll(routeFilters);
      filters.add(new ForwardServletFilter(restTemplate));
    }

    AnnotationAwareOrderComparator.sort(filters);
    DefaultServletFilterChain.create(filters).doFilter(request, response);
  }

  private RouteDefinition lookupRoute(HttpServletRequest request) {
    List<RouteDefinition> routes = gatewayProperties.getRoutes();
    return routes.stream()
        .filter(
            route ->
                route.getPredicates().stream()
                    .map(PredicateDefinition::getName)
                    .map(routeComponentLocator::getPredicate)
                    .filter(Objects::nonNull)
                    .map(
                        predicateFactory -> {
                          Object config = predicateFactory.getConfig(route);
                          return predicateFactory.apply(config);
                        })
                    .map(predicate -> predicate.test(request))
                    .findAny()
                    .orElse(false))
        .findAny()
        .orElse(null);
  }
}
