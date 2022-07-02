package com.lzhpo.panda.gateway.support;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
  default String resolve(ServerWebExchange serverWebExchange) {
    ServerHttpRequest request = serverWebExchange.getRequest();
    return Optional.ofNullable(request.getRemoteAddress())
        .map(InetSocketAddress::getAddress)
        .map(InetAddress::getHostAddress)
        .orElse(null);
  }
}
