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

import com.google.common.collect.Lists;
import com.lzhpo.panda.gateway.core.route.RouteRefreshEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class CacheRouteLocator implements RouteLocator {

  private final RouteDefinitionLocator routeDefinitionLocator;
  private final Map<String, Route> cacheRoutes = new ConcurrentHashMap<>();

  @Override
  public void onApplicationEvent(@NonNull RouteRefreshEvent event) {
    cacheRoutes.clear();
    routeDefinitionLocator
        .getRoutes()
        .toStream()
        .map(this::convert)
        .forEach(route -> cacheRoutes.put(route.getId(), route));
  }

  @Override
  public Route getRoute(String routeId) {
    return cacheRoutes.get(routeId);
  }

  @Override
  public List<Route> getRoutes() {
    return Lists.newArrayList(cacheRoutes.values()).stream()
        .sorted(Comparator.comparing(Route::getOrder))
        .collect(Collectors.toList());
  }
}
