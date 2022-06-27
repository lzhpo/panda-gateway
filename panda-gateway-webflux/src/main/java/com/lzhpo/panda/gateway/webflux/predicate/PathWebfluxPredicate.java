package com.lzhpo.panda.gateway.webflux.predicate;

import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author lzhpo
 */
public class PathWebfluxPredicate implements WebfluxPredicate {

  @Override
  public String getPrefix() {
    String suffix = WebfluxPredicate.super.getSuffix();
    return getClass().getSimpleName().replace(suffix, "");
  }

  @Override
  public boolean apply(ServerWebExchange exchange, RouteDefinition route) {
    String prefix = getPrefix();
    ServerHttpRequest request = exchange.getRequest();
    String requestPath = request.getPath().value();
    return false;
  }
}
