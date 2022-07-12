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

package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.ConfigFactory;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;

/**
 * Route predicate factory, it can create route predicate.
 *
 * @author lzhpo
 */
public interface RoutePredicateFactory<T> extends ConfigFactory<T> {

  /**
   * Use {@code config} to create route predicate.
   *
   * @param config config
   * @return created route predicate
   */
  RoutePredicate create(T config);

  /**
   * Use {@code predicateDefinition} to create route predicate.
   *
   * @param predicateDefinition predicate definition
   * @return created route predicate
   */
  default RoutePredicate create(ComponentDefinition predicateDefinition) {
    T config = getConfig(predicateDefinition);
    return create(config);
  }

  /**
   * Get current route predicate name.
   *
   * @return current route predicate name
   */
  @Override
  default String name() {
    return getClass().getSimpleName().replace(RoutePredicateFactory.class.getSimpleName(), "");
  }
}
