package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;

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

  /**
   * Sort routes.
   *
   * @return sorted routes
   */
  default List<RouteDefinition> sortRoutes(List<RouteDefinition> routes) {
    if (!CollectionUtils.isEmpty(routes)) {
      return routes.stream()
          .sorted(Comparator.comparingInt(RouteDefinition::getOrder))
          .collect(Collectors.toList());
    }
    return routes;
  }
}
