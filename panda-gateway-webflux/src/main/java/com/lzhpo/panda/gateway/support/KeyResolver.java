package com.lzhpo.panda.gateway.support;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
public interface KeyResolver {

  /**
   * Resolve key
   *
   * @param exchange {@link ServerWebExchange}
   * @return key
   */
  Mono<String> resolve(ServerWebExchange exchange);
}
