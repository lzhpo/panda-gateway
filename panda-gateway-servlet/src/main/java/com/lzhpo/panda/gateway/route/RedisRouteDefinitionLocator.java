package com.lzhpo.panda.gateway.route;

import com.google.common.collect.Lists;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class RedisRouteDefinitionLocator implements RouteDefinitionLocator {

  private final RedisTemplate<String, RouteDefinition> redisTemplate;

  @Override
  public RouteDefinition getRoute(String routeId) {
    return Optional.ofNullable(
            redisTemplate.opsForHash().get(GatewayConst.ROUTE_DEFINITION, routeId))
        .map(RouteDefinition.class::cast)
        .orElse(null);
  }

  @Override
  public List<RouteDefinition> getRoutes() {
    return Optional.of(redisTemplate.opsForHash().values(GatewayConst.ROUTE_DEFINITION))
        .filter(x -> !CollectionUtils.isEmpty(x))
        .orElse(Lists.newArrayList())
        .stream()
        .map(RouteDefinition.class::cast)
        .sorted(Comparator.comparingInt(RouteDefinition::getOrder))
        .collect(Collectors.toList());
  }

  @Override
  public void saveRoute(RouteDefinition route) {
    redisTemplate.opsForHash().put(GatewayConst.ROUTE_DEFINITION, route.getId(), route);
  }

  @Override
  public void deleteRoute(String routeId) {
    redisTemplate.opsForHash().delete(GatewayConst.ROUTE_DEFINITION, routeId);
  }
}
