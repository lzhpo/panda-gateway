package com.lzhpo.panda.gateway.handler;

import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RelationType;
import com.lzhpo.panda.gateway.core.route.RouteMetadataConst;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import com.lzhpo.panda.gateway.route.Route;
import com.lzhpo.panda.gateway.route.RouteLocator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.handler.AbstractHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Slf4j
public class GatewayRequestMapping extends AbstractHandlerMapping {

  private final RouteLocator routeLocator;
  private final GatewayRequestHandler requestHandler;

  public GatewayRequestMapping(RouteLocator routeLocator, GatewayRequestHandler requestHandler) {
    this.routeLocator = routeLocator;
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
        .map(route -> Mono.just(requestHandler))
        .orElseGet(
            () ->
                Mono.empty()
                    .then(
                        Mono.fromRunnable(
                            () -> {
                              exchange.getAttributes().remove(GatewayConst.ROUTE_ID);
                              if (log.isDebugEnabled()) {
                                log.warn("No route found.");
                              }
                            })));
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
