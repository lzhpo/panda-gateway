package com.lzhpo.panda.gateway.webflux.filter.global;

import com.lzhpo.panda.gateway.webflux.filter.DefaultWebfluxFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
public interface GlobalWebfluxFilter {

  Mono<Void> filter(ServerWebExchange exchange, DefaultWebfluxFilterChain filterChain);
}
