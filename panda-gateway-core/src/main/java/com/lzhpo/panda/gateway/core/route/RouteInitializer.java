package com.lzhpo.panda.gateway.core.route;

import java.util.List;

/**
 * @author lzhpo
 */
public interface RouteInitializer {

  /**
   * Init routes
   *
   * @param routeDefinitions routeDefinitions
   */
  void initialize(List<RouteDefinition> routeDefinitions);
}
