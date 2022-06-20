package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.core.ExtractUtils;
import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.GatewayProxyRoute;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
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
  private final GatewayProperties gatewayProperties;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    long startMillis = System.currentTimeMillis();
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().value();

    List<GatewayProxyRoute> proxyRoutes = gatewayProperties.getRoutes();
    Optional<GatewayProxyRoute> proxyRouteOptional =
        proxyRoutes.stream()
            .filter(proxyRoute -> antPathMatcher.match(proxyRoute.getMatchPath(), path))
            .findAny();

    if (proxyRouteOptional.isEmpty()) {
      return chain.filter(exchange);
    }

    GatewayProxyRoute proxyRoute = proxyRouteOptional.get();
    String filteredPath = ExtractUtils.stripPrefix(path, proxyRoute.getStripPrefix());
    String fullPath = proxyRoute.getTargetUrl() + filteredPath;
    log.info("Request [{}] match to route {}", path, proxyRoute);
    HttpMethod httpMethod = request.getMethod();
    Assert.notNull(httpMethod, "Bad request");

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
          ServerHttpResponse response = exchange.getResponse();
          response.getHeaders().putAll(clientResponse.headers().asHttpHeaders());
          response.setStatusCode(clientResponse.statusCode());
          Flux<DataBuffer> clientResDataBuffers =
              clientResponse.body(BodyExtractors.toDataBuffers());
          return response
              .writeWith(clientResDataBuffers)
              .doFinally(
                  x ->
                      log.info(
                          "Request [{}] completed, take time {} millis.",
                          fullPath,
                          System.currentTimeMillis() - startMillis));
        });
  }
}
