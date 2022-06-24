package com.lzhpo.panda.gateway.webflux.predicate;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @author lzhpo
 */
public interface ServletPredicate {

  boolean apply(ServerHttpRequest request, RouteDefinition route);

  String getPrefix();

  default String getSuffix() {
    return ServletPredicate.class.getSimpleName();
  }
}
