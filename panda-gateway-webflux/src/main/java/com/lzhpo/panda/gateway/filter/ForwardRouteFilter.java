package com.lzhpo.panda.gateway.filter;

import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RouteMetadataConst;
import com.lzhpo.panda.gateway.core.utils.ExtractUtil;
import com.lzhpo.panda.gateway.route.Route;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
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

    return headersSpec.exchangeToMono(
        clientResponse -> {
          response.setStatusCode(clientResponse.statusCode());
          Flux<DataBuffer> clientResDataBuffers =
              clientResponse.body(BodyExtractors.toDataBuffers());
          return response.writeWith(clientResDataBuffers);
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
            RouteMetadataConst.CONNECT_TIMEOUT,
            Duration.ofMillis(GatewayConst.DEFAULT_CONNECT_TIMEOUT));
    Duration responseTimeout =
        exchange.getAttributeOrDefault(
            RouteMetadataConst.RESPONSE_TIMEOUT,
            Duration.ofMillis(GatewayConst.DEFAULT_RESPONSE_TIMEOUT));

    return HttpClient.create()
        .responseTimeout(responseTimeout)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout.toMillisPart())
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
