/*
 * Copyright 2022 lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzhpo.panda.gateway.filter;

import com.lzhpo.panda.gateway.core.route.GatewayConstants;
import com.lzhpo.panda.gateway.core.route.RouteMetadataConstants;
import com.lzhpo.panda.gateway.core.utils.ExtractUtil;
import com.lzhpo.panda.gateway.route.Route;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class ForwardRouteFilter implements RouteFilter, Ordered {

  private final Route route;
  private final WebClient.Builder webClientBuilder;

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, DefaultRouteFilterChain filterChain) {
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();

    String requestPath = request.getPath().value();
    HttpMethod httpMethod = request.getMethod();
    Assert.notNull(httpMethod, "Bad request");

    requestPath = buildPathWithParams(request, requestPath);
    HttpHeaders headers = request.getHeaders();

    HttpClient httpClient = getHttpClientWithTimeout(exchange);
    ReactorClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);

    RequestBodySpec bodySpec =
        webClientBuilder
            .clientConnector(clientHttpConnector)
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

    return headersSpec
        .exchangeToMono(
            clientResponse -> {
              response.setStatusCode(clientResponse.statusCode());
              Flux<DataBuffer> clientResDataBuffers =
                  clientResponse.body(BodyExtractors.toDataBuffers());
              return response.writeWith(clientResDataBuffers);
            })
        .onErrorMap(
            WebClientRequestException.class,
            e -> {
              String message = e.getMessage();
              Throwable rootCause = e.getRootCause();
              HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
              if (rootCause instanceof ReadTimeoutException
                  || rootCause instanceof WriteTimeoutException) {
                httpStatus = HttpStatus.GATEWAY_TIMEOUT;
              }
              log.error("Http request error: {}", message, e);
              throw new ResponseStatusException(httpStatus, message, e);
            });
  }

  /**
   * Setup about some timeout.
   *
   * @param exchange {@link ServerWebExchange}
   * @return {@link HttpClient}
   */
  private HttpClient getHttpClientWithTimeout(ServerWebExchange exchange) {
    Duration connectTimeout =
        exchange.getAttributeOrDefault(
            RouteMetadataConstants.CONNECT_TIMEOUT,
            Duration.ofMillis(GatewayConstants.DEFAULT_CONNECT_TIMEOUT));
    Duration responseTimeout =
        exchange.getAttributeOrDefault(
            RouteMetadataConstants.RESPONSE_TIMEOUT,
            Duration.ofMillis(GatewayConstants.DEFAULT_RESPONSE_TIMEOUT));

    return HttpClient.create()
        .responseTimeout(responseTimeout)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.toIntExact(connectTimeout.toMillis()))
        .doOnConnected(
            conn ->
                conn.addHandlerLast(
                        new ReadTimeoutHandler(responseTimeout.toMillis(), TimeUnit.MILLISECONDS))
                    .addHandlerLast(
                        new WriteTimeoutHandler(
                            responseTimeout.toMillis(), TimeUnit.MILLISECONDS)));
  }

  /**
   * Append request params to request path.
   *
   * @param request {@link ServerHttpRequest}
   * @param fullPath full-request path
   * @return request path after appended request params
   */
  private String buildPathWithParams(ServerHttpRequest request, String fullPath) {
    Map<String, String> queryParams = request.getQueryParams().toSingleValueMap();
    if (!ObjectUtils.isEmpty(queryParams)) {
      String queryParamsInPath = ExtractUtil.mapToParamPath(queryParams);
      fullPath += "?" + queryParamsInPath;
    }
    return fullPath;
  }
}
