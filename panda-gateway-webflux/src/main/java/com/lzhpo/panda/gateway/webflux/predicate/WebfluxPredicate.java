package com.lzhpo.panda.gateway.webflux.predicate;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author lzhpo
 */
public interface WebfluxPredicate {

  boolean apply(ServerWebExchange exchange, RouteDefinition route);

  String getPrefix();

  default String getSuffix() {
    return WebfluxPredicate.class.getSimpleName();
  }
}
