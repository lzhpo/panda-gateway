package com.lzhpo.sample.gateway.servlet;

import com.lzhpo.panda.gateway.webflux.filter.DefaultRouteFilterChain;
import com.lzhpo.panda.gateway.webflux.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Component
public class ResponseGlobalFilter implements GlobalFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, DefaultRouteFilterChain filterChain) {
    ServerHttpResponse response = exchange.getResponse();
    HttpHeaders headers = response.getHeaders();
    headers.add("country", "China");
    headers.add("city", "Guangzhou");
    return filterChain.filter(exchange);
  }
}
