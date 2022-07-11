package com.lzhpo.panda.gateway.handler;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.GatewayProperties.HttpClientConfig;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RelationType;
import com.lzhpo.panda.gateway.core.route.RouteMetadataConst;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import com.lzhpo.panda.gateway.route.Route;
import com.lzhpo.panda.gateway.route.RouteLocator;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.handler.AbstractHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Slf4j
public class GatewayRequestMapping extends AbstractHandlerMapping {

  private final RouteLocator routeLocator;
  private final GatewayProperties gatewayProperties;
  private final GatewayRequestHandler requestHandler;

  public GatewayRequestMapping(
      RouteLocator routeLocator,
      GatewayProperties gatewayProperties,
      GatewayRequestHandler requestHandler) {
    this.routeLocator = routeLocator;
    this.gatewayProperties = gatewayProperties;
    this.requestHandler = requestHandler;
    // Default value is Ordered.LOWEST_PRECEDENCE
    // RequestMappingHandlerMapping order is 0
    // RouterFunctionMapping order is -1
    // ResourceHandlerRegistry default order is Ordered.LOWEST_PRECEDENCE - 1
    setOrder(1);
  }

  @Nonnull
  @Override
  protected Mono<?> getHandlerInternal(@Nonnull ServerWebExchange exchange) {
    return lookupRoute(exchange)
        .map(
            route -> {
              Map<String, String> metadata = route.getMetadata();
              HttpClientConfig httpClient = gatewayProperties.getHttpClient();

              Duration connectTimeout = getConnectTimeout(metadata, httpClient);
              Duration responseTimeout = getResponseTimeout(metadata, httpClient);

              exchange.getAttributes().put(RouteMetadataConst.CONNECT_TIMEOUT, connectTimeout);
              exchange.getAttributes().put(RouteMetadataConst.RESPONSE_TIMEOUT, responseTimeout);
              return route;
            })
        .map(route -> Mono.just(requestHandler))
        .orElseGet(
            () ->
                Mono.empty()
                    .then(
                        Mono.fromRunnable(
                            () -> {
                              exchange.getAttributes().remove(GatewayConst.ROUTE_ID);
                              exchange.getAttributes().remove(RouteMetadataConst.CONNECT_TIMEOUT);
                              exchange.getAttributes().remove(RouteMetadataConst.RESPONSE_TIMEOUT);
                              if (log.isDebugEnabled()) {
                                log.warn("No route found.");
                              }
                            })));
  }

  /**
   * Get connect timeout.
   *
   * <p>priority: route metadata configuration > gateway properties httpClient configuration
   *
   * @param metadata route metadata. unit: milliseconds
   * @param httpClient gateway properties httpClient configuration
   * @return connect timeout
   */
  private Duration getConnectTimeout(Map<String, String> metadata, HttpClientConfig httpClient) {
    return Optional.ofNullable(metadata.get(RouteMetadataConst.CONNECT_TIMEOUT))
        .filter(StringUtils::hasText)
        .map(connectTimeoutMillis -> Duration.ofMillis(Long.parseLong(connectTimeoutMillis)))
        .orElseGet(httpClient::getConnectTimeout);
  }

  /**
   * Get response timeout.
   *
   * <p>priority: route metadata configuration > gateway properties httpClient configuration
   *
   * @param metadata route metadata. unit: milliseconds
   * @param httpClient gateway properties httpClient configuration
   * @return response timeout
   */
  private Duration getResponseTimeout(Map<String, String> metadata, HttpClientConfig httpClient) {
    return Optional.ofNullable(metadata.get(RouteMetadataConst.RESPONSE_TIMEOUT))
        .filter(StringUtils::hasText)
        .map(responseTimeoutMillis -> Duration.ofMillis(Long.parseLong(responseTimeoutMillis)))
        .orElseGet(httpClient::getResponseTimeout);
  }

  /**
   * Execute route predicate, in order to find match route.
   *
   * @param exchange {@link ServerWebExchange}
   * @return matched route
   */
  private Optional<Route> lookupRoute(ServerWebExchange exchange) {
    List<Route> routes = routeLocator.getRoutes();
    return routes.stream()
        .filter(
            route -> {
              exchange.getAttributes().put(GatewayConst.ROUTE_ID, route.getId());
              List<RoutePredicate> predicates = route.getPredicates();
              Map<String, String> metadata = route.getMetadata();
              String relation = metadata.get(RouteMetadataConst.PREDICATE_RELATION);
              if (RelationType.OR.name().equalsIgnoreCase(relation)) {
                return predicates.stream().anyMatch(predicate -> predicate.test(exchange));
              }
              return predicates.stream().allMatch(predicate -> predicate.test(exchange));
            })
        .findFirst();
  }
}
