package com.lzhpo.panda.gateway.webflux.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.consts.GatewayConst;
import com.lzhpo.panda.gateway.webflux.predicate.WebfluxPredicate;
import java.util.List;
import java.util.stream.Collectors;
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
    // Can't be missing, otherwise it won't work.
    setOrder(1);
  }

  @Override
  protected Mono<?> getHandlerInternal(ServerWebExchange exchange) {
    return lookupRoute(exchange)
        .flatMap(
            routeDefinition -> {
              exchange.getAttributes().put(GatewayConst.ROUTE_DEFINITION, routeDefinition);
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
            routeDefinition ->
                getPredicates(routeDefinition).stream()
                    .map(predicate -> predicate.apply(exchange, routeDefinition))
                    .filter(Boolean.TRUE::equals)
                    .findAny()
                    .orElse(false))
        .findAny()
        .map(Mono::just)
        .orElseGet(Mono::empty);
  }

  private List<WebfluxPredicate> getPredicates(RouteDefinition routeDefinition) {
    List<String> predicates = routeDefinition.getPredicates();
    return predicates.stream()
        .map(x -> x.split("=")[0])
        .map(StrUtil::lowerFirst)
        .map(x -> x + WebfluxPredicate.class.getSimpleName())
        .map(SpringUtil::getBean)
        .map(WebfluxPredicate.class::cast)
        .collect(Collectors.toList());
  }
}
