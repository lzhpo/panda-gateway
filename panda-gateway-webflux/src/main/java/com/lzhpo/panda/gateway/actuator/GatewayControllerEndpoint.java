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

package com.lzhpo.panda.gateway.actuator;

import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.route.RouteRefreshEvent;
import com.lzhpo.panda.gateway.filter.GlobalFilter;
import com.lzhpo.panda.gateway.filter.factory.RouteFilterFactory;
import com.lzhpo.panda.gateway.predicate.factory.RoutePredicateFactory;
import com.lzhpo.panda.gateway.route.RouteComponentUtil;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
@ControllerEndpoint(id = "gateway")
public class GatewayControllerEndpoint {

  private final RouteDefinitionLocator routeDefinitionLocator;

  @GetMapping("/routes")
  public ResponseEntity<Flux<RouteDefinition>> routes() {
    return ResponseEntity.ok(routeDefinitionLocator.getRoutes());
  }

  @GetMapping("/routes/{id}")
  public ResponseEntity<Mono<RouteDefinition>> route(@PathVariable String id) {
    return ResponseEntity.ok(routeDefinitionLocator.getRoute(id));
  }

  @GetMapping("/routes/predicates")
  public Mono<ResponseEntity<List<String>>> predicates() {
    String[] predicateNames = SpringUtil.getBeanNamesForType(RoutePredicateFactory.class);
    return Mono.just(
        ResponseEntity.ok(
            Arrays.stream(predicateNames)
                .map(
                    predicateName -> SpringUtil.getBean(predicateName, RoutePredicateFactory.class))
                .map(RoutePredicateFactory::getClass)
                .map(Class::toString)
                .collect(Collectors.toList())));
  }

  @GetMapping("/routes/{routeId}/predicates")
  public Mono<ResponseEntity<List<String>>> predicates(@PathVariable String routeId) {
    List<String> predicateNames =
        getComponentDefinitionNames(
            routeId,
            RouteDefinition::getPredicates,
            componentDefinition ->
                RouteComponentUtil.getPredicateFactory(componentDefinition.getName())
                    .getClass()
                    .getName());
    return Mono.just(ResponseEntity.ok(predicateNames));
  }

  @GetMapping("/routes/filters")
  public Mono<ResponseEntity<List<String>>> filters() {
    String[] filterNames = SpringUtil.getBeanNamesForType(RouteFilterFactory.class);
    return Mono.just(
        ResponseEntity.ok(
            Arrays.stream(filterNames)
                .map(filterName -> SpringUtil.getBean(filterName, RouteFilterFactory.class))
                .map(RouteFilterFactory::getClass)
                .map(Class::toString)
                .collect(Collectors.toList())));
  }

  @GetMapping("/routes/{routeId}/filters")
  public Mono<ResponseEntity<List<String>>> filters(@PathVariable String routeId) {
    List<String> filterNames =
        getComponentDefinitionNames(
            routeId,
            RouteDefinition::getFilters,
            componentDefinition ->
                RouteComponentUtil.getFilterFactory(componentDefinition.getName())
                    .getClass()
                    .getName());
    return Mono.just(ResponseEntity.ok(filterNames));
  }

  @GetMapping("/routes/global-filters")
  public Mono<ResponseEntity<List<String>>> globalFilters() {
    String[] filterNames = SpringUtil.getBeanNamesForType(GlobalFilter.class);
    return Mono.just(
        ResponseEntity.ok(
            Arrays.stream(filterNames)
                .map(filterName -> SpringUtil.getBean(filterName, GlobalFilter.class))
                .map(GlobalFilter::getClass)
                .map(Class::toString)
                .collect(Collectors.toList())));
  }

  @PostMapping("/routes/refresh")
  public Mono<ResponseEntity<Boolean>> refresh() {
    SpringUtil.publishEvent(new RouteRefreshEvent(this));
    return Mono.just(ResponseEntity.ok(true));
  }

  private List<String> getComponentDefinitionNames(
      String routeId,
      Function<RouteDefinition, List<ComponentDefinition>> routeMapper,
      Function<ComponentDefinition, String> componentMapper) {
    return routeDefinitionLocator
        .getRoutes()
        .toStream()
        .filter(routeDefinition -> routeDefinition.getId().equals(routeId))
        .findAny()
        .map(routeMapper)
        .map(
            componentDefinitions ->
                componentDefinitions.stream().map(componentMapper).collect(Collectors.toList()))
        .orElse(Collections.emptyList());
  }
}
