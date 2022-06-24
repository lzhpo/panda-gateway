package com.lzhpo.panda.gateway.webflux.filter;

import com.lzhpo.panda.gateway.core.ExtractUtils;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.consts.GatewayConst;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class ForwardGlobalWebfluxFilter implements GlobalWebfluxFilter {

  private final WebClient webClient;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, DefaultWebfluxFilterChain filterChain) {
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();

    String requestPath = request.getPath().value();
    HttpMethod httpMethod = request.getMethod();
    Assert.notNull(httpMethod, "Bad request");

    requestPath = buildPathWithParams(request, requestPath);
    HttpHeaders headers = request.getHeaders();

    RouteDefinition routeDefinition = exchange.getAttribute(GatewayConst.ROUTE_DEFINITION);
    if (Objects.isNull(routeDefinition)) {
      return filterChain.filter(exchange);
    }

    RequestBodySpec bodySpec =
        webClient
            .method(httpMethod)
            .uri(routeDefinition.getUri() + requestPath)
            .headers(httpHeaders -> httpHeaders.addAll(headers));

    RequestHeadersSpec<?> headersSpec;
    if (ExtractUtils.requireBody(httpMethod)) {
      headersSpec = bodySpec.body(BodyInserters.fromDataBuffers(request.getBody()));
    } else {
      headersSpec = bodySpec;
    }

    return headersSpec.exchangeToMono(
        clientResponse -> {
          response.setStatusCode(clientResponse.statusCode());
          Flux<DataBuffer> clientResDataBuffers =
              clientResponse.body(BodyExtractors.toDataBuffers());
          return response.writeWith(clientResDataBuffers);
        });
  }

  private String buildPathWithParams(ServerHttpRequest request, String fullPath) {
    Map<String, String> queryParams = request.getQueryParams().toSingleValueMap();
    if (!ObjectUtils.isEmpty(queryParams)) {
      String queryParamsInPath = ExtractUtils.mapToParamPath(queryParams);
      fullPath += "?" + queryParamsInPath;
    }
    return fullPath;
  }
}
