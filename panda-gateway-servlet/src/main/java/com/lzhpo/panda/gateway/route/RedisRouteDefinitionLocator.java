package com.lzhpo.panda.gateway.route;

import com.google.common.collect.Lists;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.route.RouteRefreshEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            redisTemplate.opsForHash().get(GatewayConst.ROUTE_DEFINITION_CACHE_KEY, routeId))
        .map(RouteDefinition.class::cast)
        .orElse(null);
  }

  @Override
  public List<RouteDefinition> getRoutes() {
    return Optional.of(redisTemplate.opsForHash().values(GatewayConst.ROUTE_DEFINITION_CACHE_KEY))
        .filter(x -> !CollectionUtils.isEmpty(x))
        .orElse(Lists.newArrayList())
        .stream()
        .map(RouteDefinition.class::cast)
        .sorted(Comparator.comparingInt(RouteDefinition::getOrder))
        .collect(Collectors.toList());
  }

  @Override
  public void saveRoutes(RouteDefinition... routeDefinitions) {
    Map<String, RouteDefinition> routeDefinitionMap = new HashMap<>(routeDefinitions.length);
    for (RouteDefinition routeDefinition : routeDefinitions) {
      routeDefinitionMap.put(routeDefinition.getId(), routeDefinition);
    }

    redisTemplate.opsForHash().putAll(GatewayConst.ROUTE_DEFINITION_CACHE_KEY, routeDefinitionMap);
    publishRefreshEvent(new RouteRefreshEvent(this));
  }

  @Override
  public void deleteRoutes(String... routeIds) {
    Long deletedNum =
        redisTemplate
            .opsForHash()
            .delete(
                GatewayConst.ROUTE_DEFINITION_CACHE_KEY,
                Arrays.stream(routeIds).map(Object.class::cast).collect(Collectors.toList()));

    if (deletedNum > 0) {
      publishRefreshEvent(new RouteRefreshEvent(this));
    }
  }
}
