package com.lzhpo.panda.gateway.route;

import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzhpo
 */
@Slf4j
public class MemoryRouteDefinitionLocator implements RouteDefinitionLocator {

  private final List<RouteDefinition> routeDefinitions;

  public MemoryRouteDefinitionLocator(List<RouteDefinition> routeDefinitions) {
    validateRoute(routeDefinitions);
    this.routeDefinitions = sortRoutes(routeDefinitions);
  }

  @Override
  public RouteDefinition getRoute(String routeId) {
    return routeDefinitions.stream().filter(x -> x.getId().equals(routeId)).findAny().orElse(null);
  }

  @Override
  public List<RouteDefinition> getRoutes() {
    return routeDefinitions;
  }
}
