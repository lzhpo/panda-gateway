package com.lzhpo.panda.gateway.handler;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RelationType;
import com.lzhpo.panda.gateway.core.route.RouteMetadataConst;
import com.lzhpo.panda.gateway.filter.DefaultRouteFilterChain;
import com.lzhpo.panda.gateway.filter.ForwardRouteFilter;
import com.lzhpo.panda.gateway.filter.GlobalFilterAdapter;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import com.lzhpo.panda.gateway.route.Route;
import com.lzhpo.panda.gateway.route.RouteComponentExtractor;
import com.lzhpo.panda.gateway.route.RouteLocator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class GatewayRequestMapping extends OncePerRequestFilter implements Ordered {

  private final RouteLocator routeLocator;
  private final RestTemplate restTemplate;
  private final GatewayProperties gatewayProperties;

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

    List<GlobalFilterAdapter> globalFilters = RouteComponentExtractor.getGlobalFilterAdapters();
    List<RouteFilter> filters = new ArrayList<>(globalFilters);
    Optional<Route> routeOptional = lookupRoute(request);

    if (routeOptional.isPresent()) {
      Route route = routeOptional.get();
      List<RouteFilter> routeFilters = route.getFilters();
      filters.addAll(routeFilters);
      filters.add(new ForwardRouteFilter(route, restTemplate, gatewayProperties));
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
  private Optional<Route> lookupRoute(HttpServletRequest request) {
    List<Route> routes = routeLocator.getRoutes();
    return routes.stream()
        .filter(
            route -> {
              request.setAttribute(GatewayConst.ROUTE_ID, route.getId());
              List<RoutePredicate> predicates = route.getPredicates();
              Map<String, String> metadata = route.getMetadata();
              String relation = metadata.get(RouteMetadataConst.PREDICATE_RELATION);
              if (RelationType.OR.name().equalsIgnoreCase(relation)) {
                return predicates.stream().anyMatch(predicate -> predicate.test(request));
              }
              return predicates.stream().allMatch(predicate -> predicate.test(request));
            })
        .findFirst();
  }
}
