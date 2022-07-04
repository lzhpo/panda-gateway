package com.lzhpo.panda.gateway.route;

import com.google.common.collect.Lists;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import java.util.ArrayList;
import java.util.List;
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
  public void saveRoute(RouteDefinition route) {
    validateRoute(Lists.newArrayList(route));
    routeDefinitions.add(route);
  }

  @Override
  public void deleteRoute(String routeId) {
    routeDefinitions.removeIf(route -> route.getId().equals(routeId));
  }
}
