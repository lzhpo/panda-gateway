package com.lzhpo.panda.gateway.webflux.handler;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.RouteUtil;
import com.lzhpo.panda.gateway.core.consts.GatewayConst;
import com.lzhpo.panda.gateway.webflux.filter.DefaultWebfluxFilterChain;
import com.lzhpo.panda.gateway.webflux.filter.ForwardWebfluxFilter;
import com.lzhpo.panda.gateway.webflux.filter.GlobalWebfluxFilter;
import com.lzhpo.panda.gateway.webflux.filter.GlobalWebfluxFilterAdapter;
import com.lzhpo.panda.gateway.webflux.filter.WebfluxFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class WebfluxWebHandler implements WebHandler {

  private final WebClient.Builder webClientBuilder;
  private final List<GlobalWebfluxFilter> globalFilters;

  @Override
  public Mono<Void> handle(ServerWebExchange exchange) {
    RouteDefinition route = exchange.getAttribute(GatewayConst.ROUTE_DEFINITION);
    List<WebfluxFilter> filters = new ArrayList<>();
    globalFilters.stream().map(GlobalWebfluxFilterAdapter::new).forEach(filters::add);

    if (Objects.nonNull(route)) {
      List<WebfluxFilter> routeFilters = RouteUtil.parseFilters(route, WebfluxFilter.class);
      filters.addAll(routeFilters);
      AnnotationAwareOrderComparator.sort(filters);
      ForwardWebfluxFilter forwardFilter = new ForwardWebfluxFilter(webClientBuilder);
      filters.add(forwardFilter);
    }

    return new DefaultWebfluxFilterChain(filters).filter(exchange);
  }
}
