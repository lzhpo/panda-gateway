package com.lzhpo.panda.gateway.filter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter will apply to all routes.
 *
 * @author lzhpo
 */
public interface GlobalFilter {

  /**
   * Execute global filter.
   *
   * @param exchange {@link ServerWebExchange}
   * @param filterChain {@link DefaultRouteFilterChain}
   * @return Void
   */
  Mono<Void> filter(ServerWebExchange exchange, DefaultRouteFilterChain filterChain);
}
