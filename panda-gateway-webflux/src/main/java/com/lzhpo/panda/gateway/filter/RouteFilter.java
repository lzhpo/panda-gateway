package com.lzhpo.panda.gateway.filter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Route filter just apply current route.
 *
 * @author lzhpo
 */
public interface RouteFilter {

  /**
   * Execute route filter.
   *
   * @param exchange {@link ServerWebExchange}
   * @param filterChain {@link DefaultRouteFilterChain}
   * @return Void
   */
  Mono<Void> filter(ServerWebExchange exchange, DefaultRouteFilterChain filterChain);
}
