package com.lzhpo.panda.gateway.webflux.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Slf4j
public class LogGlobalWebfluxFilter implements GlobalWebfluxFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, DefaultWebfluxFilterChain filterChain) {
    log.info("Request [{}]", exchange.getRequest().getPath().value());
    return filterChain.filter(exchange);
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
