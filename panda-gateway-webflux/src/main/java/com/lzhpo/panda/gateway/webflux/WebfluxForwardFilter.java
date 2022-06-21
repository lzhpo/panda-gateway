package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.core.ExtractUtils;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.loadbalancer.RouteLoadBalancer;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class WebfluxForwardFilter implements WebFilter {

  private final WebClient webClient;
  private final RouteLoadBalancer routeLoadBalancer;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();

    String requestPath = request.getPath().value();
    RouteDefinition proxyRoute = routeLoadBalancer.choose(requestPath);
    if (Objects.isNull(proxyRoute) || response.isCommitted()) {
      return chain.filter(exchange);
    }

    String filteredPath = ExtractUtils.stripPrefix(requestPath, proxyRoute.getStripPrefix());
    String fullPath = proxyRoute.getTargetUrl() + filteredPath;
    HttpMethod httpMethod = request.getMethod();
    Assert.notNull(httpMethod, "Bad request");

    fullPath = buildPathWithParams(request, fullPath);
    RequestBodySpec bodySpec =
        webClient
            .method(httpMethod)
            .uri(fullPath)
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()));

    RequestHeadersSpec<?> headersSpec;
    if (ExtractUtils.requireBody(httpMethod)) {
      headersSpec = bodySpec.body(BodyInserters.fromDataBuffers(request.getBody()));
    } else {
      headersSpec = bodySpec;
    }

    return headersSpec.exchangeToMono(
        clientResponse -> {
          response.getHeaders().putAll(clientResponse.headers().asHttpHeaders());
          response.setStatusCode(clientResponse.statusCode());
          Flux<DataBuffer> clientResDataBuffers =
              clientResponse.body(BodyExtractors.toDataBuffers());
          return response.writeWith(clientResDataBuffers);
        });
  }

  private String buildPathWithParams(ServerHttpRequest request, String fullPath) {
    Map<String, String> queryParams = request.getQueryParams().toSingleValueMap();
    if (!ObjectUtils.isEmpty(queryParams)) {
      String queryParamsInPath = ExtractUtils.mapToUrl(queryParams);
      fullPath += "?" + queryParamsInPath;
    }
    return fullPath;
  }
}
