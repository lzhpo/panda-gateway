package com.lzhpo.panda.gateway.route;

import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class RedisRouteDefinitionLocator implements RouteDefinitionLocator {

  private final ReactiveRedisTemplate<String, RouteDefinition> redisTemplate;

  @Override
  public Mono<RouteDefinition> getRoute(String routeId) {
    return redisTemplate
        .opsForHash()
        .get(GatewayConst.ROUTE_DEFINITION, routeId)
        .cast(RouteDefinition.class);
  }

  @Override
  public Flux<RouteDefinition> getRoutes() {
    return redisTemplate
        .opsForHash()
        .values(GatewayConst.ROUTE_DEFINITION)
        .cast(RouteDefinition.class);
  }

  @Override
  public Mono<Void> saveRoute(RouteDefinition route) {
    return redisTemplate
        .opsForHash()
        .put(GatewayConst.ROUTE_DEFINITION, route.getId(), route)
        .flatMap(x -> Mono.empty());
  }

  @Override
  public Mono<Void> deleteRoute(String routeId) {
    return redisTemplate
        .opsForHash()
        .remove(GatewayConst.ROUTE_DEFINITION, routeId)
        .flatMap(x -> Mono.empty());
  }
}
