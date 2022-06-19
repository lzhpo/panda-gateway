package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.core.ForwardTargetUtils;
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
public class WebClientForwardFilter implements WebFilter {

  private final WebClient webClient;
  private final GatewayProperties gatewayProperties;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    long startMillis = System.currentTimeMillis();
    ServerHttpRequest request = exchange.getRequest();
    String requestPath = request.getPath().value();

    List<GatewayProxyRoute> proxyRoutes = gatewayProperties.getRoutes();
    Optional<GatewayProxyRoute> proxyRouteOptional =
        proxyRoutes.stream()
            .filter(proxyRoute -> antPathMatcher.match(proxyRoute.getMatchPath(), requestPath))
            .findAny();

    if (proxyRouteOptional.isPresent()) {
      GatewayProxyRoute proxyRoute = proxyRouteOptional.get();
      String stripPrefixedRequestPath =
          ForwardTargetUtils.stripPrefix(requestPath, proxyRoute.getStripPrefix());
      String fullRequestPath = proxyRoute.getTargetUrl() + stripPrefixedRequestPath;
      log.info("Request [{}] match to route {}", requestPath, proxyRoute);

      HttpMethod httpMethod = request.getMethod();
      Assert.notNull(httpMethod, "Bad request");

      RequestBodySpec requestBodySpec =
          webClient
              .method(httpMethod)
              .uri(fullRequestPath)
              .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()));

      RequestHeadersSpec<?> requestHeadersSpec;
      if (requireBody(httpMethod)) {
        requestHeadersSpec = requestBodySpec.body(BodyInserters.fromDataBuffers(request.getBody()));
      } else {
        requestHeadersSpec = requestBodySpec;
      }

      return requestHeadersSpec.exchangeToMono(
          clientResponse -> {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().putAll(clientResponse.headers().asHttpHeaders());
            response.setStatusCode(clientResponse.statusCode());
            Flux<DataBuffer> clientResDataBuffers =
                clientResponse.body(BodyExtractors.toDataBuffers());
            return response
                .writeWith(clientResDataBuffers)
                .doFinally(
                    signalType ->
                        log.info(
                            "Request [{}] completed, take time {} millis.",
                            fullRequestPath,
                            System.currentTimeMillis() - startMillis));
          });
    }

    return chain.filter(exchange);
  }

  private boolean requireBody(HttpMethod method) {
    switch (method) {
      case PUT:
      case POST:
      case PATCH:
        return true;
      default:
        return false;
    }
  }
}
