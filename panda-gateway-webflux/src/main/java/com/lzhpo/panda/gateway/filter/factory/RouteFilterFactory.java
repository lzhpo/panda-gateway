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

package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.ConfigFactory;
import com.lzhpo.panda.gateway.filter.RouteFilter;

/**
 * Route filter factory, it can create route filter.
 *
 * @author lzhpo
 */
public interface RouteFilterFactory<T> extends ConfigFactory<T> {

  /**
   * Use {@code config} to create route filter.
   *
   * @param config config
   * @return created route filter
   */
  RouteFilter create(T config);

  /**
   * Use {@code componentDefinition} to create route filter.
   *
   * @param componentDefinition componentDefinition
   * @return created route filter
   */
  default RouteFilter create(ComponentDefinition componentDefinition) {
    T config = getConfig(componentDefinition);
    return create(config);
  }

  /**
   * Get current route filter name.
   *
   * @return current route filter name
   */
  @Override
  default String name() {
    return getClass().getSimpleName().replace(RouteFilterFactory.class.getSimpleName(), "");
  }
}
