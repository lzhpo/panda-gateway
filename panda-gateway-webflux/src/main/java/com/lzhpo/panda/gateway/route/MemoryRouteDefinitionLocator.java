package com.lzhpo.panda.gateway.route;

import com.google.common.collect.Lists;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Slf4j
public class MemoryRouteDefinitionLocator implements RouteDefinitionLocator {

  private final List<RouteDefinition> routeDefinitions = new ArrayList<>();

  @Override
  public Mono<RouteDefinition> getRoute(String routeId) {
    return routeDefinitions.stream()
        .filter(x -> x.getId().equals(routeId))
        .findAny()
        .map(Mono::just)
        .orElseGet(Mono::empty);
  }

  @Override
  public Flux<RouteDefinition> getRoutes() {
    return sortRoutes(routeDefinitions);
  }

  @Override
  public Mono<Void> saveRoute(RouteDefinition route) {
    return validateRoute(Lists.newArrayList(route)).doOnNext(x -> routeDefinitions.add(route));
  }

  @Override
  public Mono<Void> deleteRoute(String routeId) {
    return Mono.just(routeDefinitions.removeIf(route -> route.getId().equals(routeId)))
        .flatMap(x -> Mono.empty());
  }
}
