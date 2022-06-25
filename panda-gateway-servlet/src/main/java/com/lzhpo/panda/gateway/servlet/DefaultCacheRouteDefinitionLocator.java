package com.lzhpo.panda.gateway.servlet;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import java.util.List;

/**
 * @author lzhpo
 */
public class DefaultCacheRouteDefinitionLocator implements RouteDefinitionLocator {

  private final List<RouteDefinition> routeDefinitions;

  public DefaultCacheRouteDefinitionLocator(List<RouteDefinition> routeDefinitions) {
    this.routeDefinitions = routeDefinitions;
  }

  @Override
  public List<RouteDefinition> getRoutes() {
    return routeDefinitions;
  }
}
