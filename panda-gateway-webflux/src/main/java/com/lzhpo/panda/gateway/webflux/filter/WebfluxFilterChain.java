package com.lzhpo.panda.gateway.webflux.filter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
public interface WebfluxFilterChain {

  Mono<Void> filter(ServerWebExchange exchange);
}