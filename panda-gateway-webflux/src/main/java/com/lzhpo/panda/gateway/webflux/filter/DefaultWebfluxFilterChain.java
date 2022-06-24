package com.lzhpo.panda.gateway.webflux.filter;

import java.util.List;
import lombok.Getter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Getter
public class DefaultWebfluxFilterChain implements WebfluxFilterChain {

  private final int index;
  private final List<WebfluxFilter> filters;

  public DefaultWebfluxFilterChain(List<WebfluxFilter> filters) {
    this.filters = filters;
    this.index = 0;
  }

  private DefaultWebfluxFilterChain(DefaultWebfluxFilterChain parent, int index) {
    this.filters = parent.getFilters();
    this.index = index;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange) {
    return Mono.defer(
        () -> {
          if (index < filters.size()) {
            WebfluxFilter filter = filters.get(index);
            DefaultWebfluxFilterChain chain = new DefaultWebfluxFilterChain(this, index + 1);
            return filter.filter(exchange, chain);
          } else {
            return Mono.empty();
          }
        });
  }
}
