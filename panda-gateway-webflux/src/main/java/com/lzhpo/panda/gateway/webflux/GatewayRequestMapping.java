package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import java.util.List;
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

  private final GatewayRequestHandler webHandler;
  private final RouteDefinitionLocator routeDefinitionLocator;

  public GatewayRequestMapping(
      GatewayRequestHandler webHandler, RouteDefinitionLocator routeDefinitionLocator) {
    this.webHandler = webHandler;
    this.routeDefinitionLocator = routeDefinitionLocator;
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
        .flatMap(
            route -> {
              exchange.getAttributes().put(GatewayConst.ROUTE_DEFINITION, route);
              return Mono.just(webHandler);
            })
        .switchIfEmpty(
            Mono.empty()
                .then(
                    Mono.fromRunnable(
                        () -> {
                          exchange.getAttributes().remove(GatewayConst.ROUTE_DEFINITION);
                          if (log.isDebugEnabled()) {
                            log.warn("No route found.");
                          }
                        })));
  }

  private Mono<RouteDefinition> lookupRoute(ServerWebExchange exchange) {
    List<RouteDefinition> routes = routeDefinitionLocator.getRoutes();
    return routes.stream()
        .peek(route -> exchange.getAttributes().put(GatewayConst.ROUTE_DEFINITION, route))
        .filter(
            route ->
                route.getPredicates().stream()
                    .map(
                        predicateDefinition -> {
                          String predicateName = predicateDefinition.getName();
                          return Optional.ofNullable(
                                  routeDefinitionLocator.getPredicateFactory(predicateName))
                              .map(
                                  predicateFactory ->
                                      predicateFactory.create(predicateDefinition).test(exchange))
                              .orElseGet(
                                  () -> {
                                    log.error("Not found [{}] predicateFactory.", predicateName);
                                    return false;
                                  });
                        })
                    .filter(Boolean.TRUE::equals)
                    .findAny()
                    .orElse(false))
        .findFirst()
        .map(Mono::just)
        .orElseGet(Mono::empty);
  }
}
