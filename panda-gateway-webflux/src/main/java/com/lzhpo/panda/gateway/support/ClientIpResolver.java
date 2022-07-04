package com.lzhpo.panda.gateway.support;

import org.springframework.web.server.ServerWebExchange;

/**
 * @author lzhpo
 */
public interface ClientIpResolver {

  /**
   * Resolve client ip from {@code request}
   *
   * @param serverWebExchange {@link ServerWebExchange}
   * @return client ip
   */
  String resolve(ServerWebExchange serverWebExchange);
}
