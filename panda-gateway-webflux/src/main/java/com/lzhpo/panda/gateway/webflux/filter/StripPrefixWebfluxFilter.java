package com.lzhpo.panda.gateway.webflux.filter;

import com.lzhpo.panda.gateway.core.ExtractUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
public class StripPrefixWebfluxFilter implements WebfluxFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, DefaultWebfluxFilterChain filterChain) {
    ServerHttpRequest request = exchange.getRequest();
    String requestPath = request.getPath().value();
    return filterChain.filter(
        exchange
            .mutate()
            .request(builder -> builder.path(ExtractUtils.stripPrefix(requestPath, 2)))
            .build());
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
