package com.lzhpo.panda.gateway.webflux.handler;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.utils.RouteUtil;
import com.lzhpo.panda.gateway.webflux.predicate.WebfluxPredicate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.handler.AbstractHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Slf4j
public class WebfluxHandlerMapping extends AbstractHandlerMapping {

  private final WebfluxWebHandler webHandler;
  private final GatewayProperties gatewayProperties;

  public WebfluxHandlerMapping(WebfluxWebHandler webHandler, GatewayProperties gatewayProperties) {
    this.webHandler = webHandler;
    this.gatewayProperties = gatewayProperties;
    // Default value is Ordered.LOWEST_PRECEDENCE
    // RequestMappingHandlerMapping order is 0
    // RouterFunctionMapping order is -1
    // ResourceHandlerRegistry default order is Ordered.LOWEST_PRECEDENCE - 1
    setOrder(1);
  }

  @Override
  protected Mono<?> getHandlerInternal(ServerWebExchange exchange) {
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
    List<RouteDefinition> routes = gatewayProperties.getRoutes();
    return routes.stream()
        .filter(
            route ->
                RouteUtil.parsePredicates(route, WebfluxPredicate.class).stream()
                    .map(predicate -> predicate.apply(exchange, route))
                    .filter(Boolean.TRUE::equals)
                    .findAny()
                    .orElse(false))
        .findAny()
        .map(Mono::just)
        .orElseGet(Mono::empty);
  }
}
