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

import cn.hutool.core.util.ArrayUtil;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.route.RouteInitializer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class DefaultRouteInitializer implements RouteInitializer {

  private final RouteDefinitionLocator routeDefinitionLocator;

  @Override
  public void initialize(List<RouteDefinition> routeDefinitions) {
    if (!CollectionUtils.isEmpty(routeDefinitions)) {
      routeDefinitionLocator.saveRoutes(ArrayUtil.toArray(routeDefinitions, RouteDefinition.class));
    }
  }
}
