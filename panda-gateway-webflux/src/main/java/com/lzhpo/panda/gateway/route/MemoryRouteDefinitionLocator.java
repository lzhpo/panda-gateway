package com.lzhpo.panda.gateway.route;

import com.google.common.collect.Lists;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.route.RouteRefreshEvent;
import java.util.ArrayList;
import java.util.Iterator;
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
  public Mono<Boolean> saveRoutes(RouteDefinition... routeDefinitions) {
    return validateRoute(routeDefinitions)
        .doOnNext(
            x -> {
              this.routeDefinitions.addAll(Lists.newArrayList(routeDefinitions));
              publishRefreshEvent(new RouteRefreshEvent(this));
            });
  }

  @Override
  public Mono<Long> deleteRoutes(String... routeIds) {
    long deletedNum = 0L;
    Iterator<RouteDefinition> iterator = routeDefinitions.iterator();
    while (iterator.hasNext()) {
      RouteDefinition routeDefinition = iterator.next();
      for (String routeId : routeIds) {
        if (routeDefinition.getId().equals(routeId)) {
          iterator.remove();
          deletedNum++;
          break;
        }
      }
    }

    if (deletedNum > 0) {
      publishRefreshEvent(new RouteRefreshEvent(this));
    }

    return Mono.just(deletedNum);
  }
}
