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

import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.route.RouteRefreshEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzhpo
 */
@Slf4j
public class MemoryRouteDefinitionLocator implements RouteDefinitionLocator {

  private final List<RouteDefinition> routeDefinitions = new ArrayList<>();

  @Override
  public RouteDefinition getRoute(String routeId) {
    return routeDefinitions.stream().filter(x -> x.getId().equals(routeId)).findAny().orElse(null);
  }

  @Override
  public List<RouteDefinition> getRoutes() {
    return sortRoutes(routeDefinitions);
  }

  @Override
  public void saveRoutes(RouteDefinition... routeDefinitions) {
    List<RouteDefinition> finalRouteDefinitions =
        Arrays.stream(routeDefinitions).collect(Collectors.toList());
    validateRoute(finalRouteDefinitions);
    this.routeDefinitions.addAll(finalRouteDefinitions);
    publishRefreshEvent(new RouteRefreshEvent(this));
  }

  @Override
  public void deleteRoutes(String... routeIds) {
    long deletedNum = 0L;
    Iterator<RouteDefinition> iterator = routeDefinitions.iterator();
    while (iterator.hasNext()) {
      RouteDefinition routeDefinition = iterator.next();
      for (String routeId : routeIds) {
        if (routeDefinition.getId().equals(routeId)) {
          iterator.remove();
          deletedNum++;
        }
      }
    }

    if (deletedNum > 0) {
      publishRefreshEvent(new RouteRefreshEvent(this));
    }
  }
}
