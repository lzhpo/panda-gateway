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

import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.route.RouteRefreshEvent;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import com.lzhpo.panda.gateway.filter.factory.RouteFilterFactory;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import com.lzhpo.panda.gateway.predicate.factory.RoutePredicateFactory;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * @author lzhpo
 */
public interface RouteLocator extends ApplicationListener<RouteRefreshEvent> {

  /**
   * Get {@link Route} by routeId.
   *
   * @param routeId routeId
   * @return {@link Route}
   */
  Route getRoute(String routeId);

  /**
   * Get all {@link Route}
   *
   * @return all {@link Route}
   */
  List<Route> getRoutes();

  /**
   * Convert {@link RouteDefinition} to {@link Route}
   *
   * @param routeDefinition {@link RouteDefinition}
   * @return {@link Route}
   */
  default Route convert(RouteDefinition routeDefinition) {
    Route route = new Route();
    BeanUtils.copyProperties(routeDefinition, route);

    List<ComponentDefinition> predicateDefinitions = routeDefinition.getPredicates();
    List<RoutePredicate> predicates =
        predicateDefinitions.stream()
            .map(
                predicateDefinition -> {
                  String name = predicateDefinition.getName();
                  RoutePredicateFactory<Object> factory =
                      RouteComponentExtractor.getPredicateFactory(name);
                  return factory.create(predicateDefinition);
                })
            .collect(Collectors.toList());
    AnnotationAwareOrderComparator.sort(predicates);
    route.setPredicates(predicates);

    List<ComponentDefinition> filterDefinitions = routeDefinition.getFilters();
    List<RouteFilter> filters =
        filterDefinitions.stream()
            .map(
                filterDefinition -> {
                  String name = filterDefinition.getName();
                  RouteFilterFactory<Object> factory =
                      RouteComponentExtractor.getFilterFactory(name);
                  return factory.create(filterDefinition);
                })
            .collect(Collectors.toList());
    AnnotationAwareOrderComparator.sort(filters);
    route.setFilters(filters);
    return route;
  }
}
