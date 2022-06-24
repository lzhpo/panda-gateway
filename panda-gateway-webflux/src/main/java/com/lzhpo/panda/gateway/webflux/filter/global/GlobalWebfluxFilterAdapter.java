package com.lzhpo.panda.gateway.webflux.filter.global;

import com.lzhpo.panda.gateway.webflux.filter.DefaultWebfluxFilterChain;
import com.lzhpo.panda.gateway.webflux.filter.WebfluxFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class GlobalWebfluxFilterAdapter implements WebfluxFilter {

  private final GlobalWebfluxFilter globalFilter;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, DefaultWebfluxFilterChain filterChain) {
    return globalFilter.filter(exchange, filterChain);
  }
}
