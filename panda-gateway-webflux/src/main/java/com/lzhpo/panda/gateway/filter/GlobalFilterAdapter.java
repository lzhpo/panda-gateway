package com.lzhpo.panda.gateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class GlobalFilterAdapter implements RouteFilter {

  private final GlobalFilter globalFilter;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, DefaultRouteFilterChain filterChain) {
    return globalFilter.filter(exchange, filterChain);
  }
}
