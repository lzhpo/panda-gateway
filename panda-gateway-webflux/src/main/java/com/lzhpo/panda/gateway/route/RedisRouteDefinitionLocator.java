/*
 * Copyright 2022 lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzhpo.panda.gateway.route;

import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.route.RouteRefreshEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
        .get(GatewayConst.ROUTE_DEFINITION_CACHE_KEY, routeId)
        .cast(RouteDefinition.class);
  }

  @Override
  public Flux<RouteDefinition> getRoutes() {
    return redisTemplate
        .opsForHash()
        .values(GatewayConst.ROUTE_DEFINITION_CACHE_KEY)
        .cast(RouteDefinition.class);
  }

  @Override
  public Mono<Boolean> saveRoutes(RouteDefinition... routeDefinitions) {
    return validateRoute(routeDefinitions)
        .flatMap(
            x -> {
              Map<String, RouteDefinition> routeDefinitionMap =
                  new HashMap<>(routeDefinitions.length);
              for (RouteDefinition routeDefinition : routeDefinitions) {
                routeDefinitionMap.put(routeDefinition.getId(), routeDefinition);
              }
              return redisTemplate
                  .opsForHash()
                  .putAll(GatewayConst.ROUTE_DEFINITION_CACHE_KEY, routeDefinitionMap)
                  .doOnNext(b -> publishRefreshEvent(new RouteRefreshEvent(this)));
            });
  }

  @Override
  public Mono<Long> deleteRoutes(String... routeIds) {
    return redisTemplate
        .opsForHash()
        .remove(GatewayConst.ROUTE_DEFINITION_CACHE_KEY, Arrays.stream(routeIds).toArray())
        .doOnNext(
            deletedNum -> {
              if (deletedNum > 0) {
                publishRefreshEvent(new RouteRefreshEvent(this));
              }
            });
  }
}
