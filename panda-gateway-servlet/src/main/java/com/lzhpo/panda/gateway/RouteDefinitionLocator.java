package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import java.util.List;

/**
 * @author lzhpo
 */
public interface RouteDefinitionLocator extends RouteComponentLocator {

  /**
   * To get routes.
   *
   * @return routes
   */
  List<RouteDefinition> getRoutes();
}
