package com.lzhpo.panda.gateway.filter;

import java.util.List;
import lombok.Getter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Getter
public class DefaultRouteFilterChain implements RouteFilterChain {

  private final int index;
  private final List<RouteFilter> filters;

  public DefaultRouteFilterChain(List<RouteFilter> filters) {
    this.filters = filters;
    this.index = 0;
  }

  private DefaultRouteFilterChain(DefaultRouteFilterChain parent, int index) {
    this.filters = parent.getFilters();
    this.index = index;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange) {
    return Mono.defer(
        () -> {
          if (index < filters.size()) {
            RouteFilter filter = filters.get(index);
            DefaultRouteFilterChain chain = new DefaultRouteFilterChain(this, index + 1);
            return filter.filter(exchange, chain);
          } else {
            return Mono.empty();
          }
        });
  }
}
