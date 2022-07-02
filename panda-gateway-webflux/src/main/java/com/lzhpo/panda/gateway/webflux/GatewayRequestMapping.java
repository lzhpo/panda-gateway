package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.core.exception.GatewayCustomException;
import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RelationType;
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

  /**
   * Execute route predicate, in order to find match route.
   *
   * @param exchange {@link ServerWebExchange}
   * @return matched route
   */
  private Mono<RouteDefinition> lookupRoute(ServerWebExchange exchange) {
    List<RouteDefinition> routes = routeDefinitionLocator.getRoutes();
    return routes.stream()
        .filter(
            route -> {
              exchange.getAttributes().put(GatewayConst.ROUTE_DEFINITION, route);
              RelationType relationType = route.getEnhances().getPredicatesRelation();
              switch (relationType) {
                case AND:
                  return route.getPredicates().stream()
                      .allMatch(
                          predicateDefinition -> testPredicate(exchange, predicateDefinition));
                case OR:
                  return route.getPredicates().stream()
                      .anyMatch(
                          predicateDefinition -> testPredicate(exchange, predicateDefinition));
                default:
                  throw new GatewayCustomException("Not support relation type " + relationType);
              }
            })
        .findFirst()
        .map(Mono::just)
        .orElseGet(Mono::empty);
  }

  /**
   * Execute predicate
   *
   * @param exchange {@link ServerWebExchange}
   * @param predicateDefinition {@link ComponentDefinition}
   * @return test predicate result
   */
  private Boolean testPredicate(
      ServerWebExchange exchange, ComponentDefinition predicateDefinition) {
    String predicateName = predicateDefinition.getName();
    return Optional.ofNullable(routeDefinitionLocator.getPredicateFactory(predicateName))
        .map(predicateFactory -> predicateFactory.create(predicateDefinition).test(exchange))
        .orElseGet(
            () -> {
              log.error("Not found [{}] predicateFactory.", predicateName);
              return false;
            });
  }
}
