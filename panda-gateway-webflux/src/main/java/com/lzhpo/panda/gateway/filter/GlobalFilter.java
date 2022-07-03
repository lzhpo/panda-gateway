package com.lzhpo.panda.gateway.filter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
public interface GlobalFilter {

  Mono<Void> filter(ServerWebExchange exchange, DefaultRouteFilterChain filterChain);
}