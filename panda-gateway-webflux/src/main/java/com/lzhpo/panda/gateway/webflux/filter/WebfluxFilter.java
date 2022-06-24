package com.lzhpo.panda.gateway.webflux.filter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
public interface WebfluxFilter {

  Mono<Void> filter(ServerWebExchange exchange, DefaultWebfluxFilterChain filterChain);
}
