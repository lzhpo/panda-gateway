package com.lzhpo.panda.gateway.webflux.filter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
public interface RouteFilter {

  Mono<Void> filter(ServerWebExchange exchange, DefaultRouteFilterChain filterChain);
}