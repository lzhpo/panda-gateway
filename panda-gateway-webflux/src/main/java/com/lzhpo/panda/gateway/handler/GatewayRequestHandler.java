package com.lzhpo.panda.gateway.handler;

import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.filter.DefaultRouteFilterChain;
import com.lzhpo.panda.gateway.filter.ForwardRouteFilter;
import com.lzhpo.panda.gateway.filter.GlobalFilterAdapter;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import com.lzhpo.panda.gateway.route.Route;
import com.lzhpo.panda.gateway.route.RouteComponentExtractor;
import com.lzhpo.panda.gateway.route.RouteLocator;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class GatewayRequestHandler implements WebHandler {

  private final RouteLocator routeLocator;
  private final WebClient.Builder webClientBuilder;

  @Override
  public Mono<Void> handle(ServerWebExchange exchange) {
    String routeId = exchange.getAttribute(GatewayConst.ROUTE_ID);
    List<GlobalFilterAdapter> globalFilters = RouteComponentExtractor.getGlobalFilterAdapters();
    List<RouteFilter> filters = new ArrayList<>(globalFilters);

    if (StringUtils.hasText(routeId)) {
      Route route = routeLocator.getRoute(routeId);
      Assert.notNull(route, "Cannot find route[" + routeId + "]");
      filters.addAll(route.getFilters());
      filters.add(new ForwardRouteFilter(route, webClientBuilder));
    }

    AnnotationAwareOrderComparator.sort(filters);
    return new DefaultRouteFilterChain(filters).filter(exchange);
  }
}
