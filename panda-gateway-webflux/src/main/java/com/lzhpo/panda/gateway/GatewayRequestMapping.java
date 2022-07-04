package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.core.exception.GatewayCustomException;
import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RelationType;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.route.RouteComponentUtil;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
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
        .flatMap(route -> Mono.just(webHandler))
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
    return routeDefinitionLocator
        .getRoutes()
        .filterWhen(
            route -> {
              exchange.getAttributes().put(GatewayConst.ROUTE_DEFINITION, route);
              RelationType relationType = route.getEnhances().getPredicatesRelation();
              switch (relationType) {
                case AND:
                  return Mono.just(
                      route.getPredicates().stream()
                          .allMatch(
                              predicateDefinition -> testPredicate(exchange, predicateDefinition)));
                case OR:
                  return Mono.just(
                      route.getPredicates().stream()
                          .anyMatch(
                              predicateDefinition -> testPredicate(exchange, predicateDefinition)));
                default:
                  throw new GatewayCustomException("Not support relation type " + relationType);
              }
            })
        .next();
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
    return RouteComponentUtil.getPredicateFactory(predicateName)
        .create(predicateDefinition)
        .test(exchange);
  }
}
