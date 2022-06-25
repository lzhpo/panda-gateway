package com.lzhpo.panda.gateway.webflux.predicate;

import com.lzhpo.panda.gateway.core.Route;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author lzhpo
 */
public interface WebfluxPredicate {

  boolean apply(ServerWebExchange exchange, Route route);

  String getPrefix();

  default String getSuffix() {
    return WebfluxPredicate.class.getSimpleName();
  }
}
