package com.lzhpo.panda.gateway.filter;

import com.lzhpo.panda.gateway.core.utils.ExtractUtil;
import com.lzhpo.panda.gateway.route.Route;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
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
public class ForwardRouteFilter implements RouteFilter, Ordered {

  private final Route route;
  private final WebClient.Builder webClientBuilder;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, DefaultRouteFilterChain filterChain) {
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();

    String requestPath = request.getPath().value();
    HttpMethod httpMethod = request.getMethod();
    Assert.notNull(httpMethod, "Bad request");

    requestPath = buildPathWithParams(request, requestPath);
    HttpHeaders headers = request.getHeaders();

    RequestBodySpec bodySpec =
        webClientBuilder
            .build()
            .method(httpMethod)
            .uri(route.getUri() + requestPath)
            .headers(httpHeaders -> httpHeaders.addAll(headers));

    RequestHeadersSpec<?> headersSpec;
    if (ExtractUtil.requireBody(httpMethod)) {
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
      String queryParamsInPath = ExtractUtil.mapToParamPath(queryParams);
      fullPath += "?" + queryParamsInPath;
    }
    return fullPath;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
