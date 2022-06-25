package com.lzhpo.panda.gateway.servlet.filter;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.Route;
import com.lzhpo.panda.gateway.core.RouteUtil;
import com.lzhpo.panda.gateway.core.consts.GatewayConst;
import com.lzhpo.panda.gateway.servlet.filter.support.GlobalServletFilterAdapter;
import com.lzhpo.panda.gateway.servlet.predicate.ServletPredicate;
import java.util.ArrayList;
import java.util.List;
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
  private final List<GlobalServletFilter> globalFilters;

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

    List<ServletFilter> filters = new ArrayList<>();
    globalFilters.stream().map(GlobalServletFilterAdapter::new).forEach(filters::add);
    Route route = lookupRoute(request, response);

    if (!ObjectUtils.isEmpty(route)) {
      request.setAttribute(GatewayConst.ROUTE_DEFINITION, route);
      List<ServletFilter> routeFilters = RouteUtil.parseFilters(route, ServletFilter.class);
      filters.addAll(routeFilters);
      filters.add(new ForwardServletFilter(restTemplate));
    }

    AnnotationAwareOrderComparator.sort(filters);
    DefaultServletFilterChain.create(filters).doFilter(request, response);
  }

  private Route lookupRoute(HttpServletRequest request, HttpServletResponse response) {
    List<Route> routes = gatewayProperties.getRoutes();
    return routes.stream()
        .filter(
            route ->
                RouteUtil.parsePredicates(route, ServletPredicate.class).stream()
                    .map(predicate -> predicate.apply(request, response, route))
                    .filter(Boolean.TRUE::equals)
                    .findAny()
                    .orElse(false))
        .findAny()
        .orElse(null);
  }
}
