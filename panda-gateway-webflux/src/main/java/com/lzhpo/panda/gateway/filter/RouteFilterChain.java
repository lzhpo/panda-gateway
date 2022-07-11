package com.lzhpo.panda.gateway.filter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filter chain for route.
 *
 * @author lzhpo
 */
public interface RouteFilterChain {

  /**
   * Execute the next filter of this filter chain.
   *
   * @param exchange {@link ServerWebExchange}
   * @return Void
   */
  Mono<Void> filter(ServerWebExchange exchange);
}
