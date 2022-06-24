package com.lzhpo.panda.gateway.webflux.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
public interface WebfluxFilter {

  Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain);
}
