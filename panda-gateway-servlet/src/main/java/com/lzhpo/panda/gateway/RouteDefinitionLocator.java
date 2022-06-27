package com.lzhpo.panda.gateway;

import cn.hutool.core.util.StrUtil;
import com.lzhpo.panda.gateway.core.GatewayCustomException;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.ValidateUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.Assert;

/**
 * @author lzhpo
 */
public interface RouteDefinitionLocator extends RouteComponentLocator {

  /**
   * According to routeId to get route.
   *
   * @param routeId routeId
   * @return route
   */
  RouteDefinition getRoute(String routeId);

  /**
   * To get routes.
   *
   * @return routes
   */
  List<RouteDefinition> getRoutes();

  /**
   * Validate route
   *
   * @param routes routes
   */
  default void validateRoute(List<RouteDefinition> routes) {
    Assert.notEmpty(routes, "routes cannot empty.");
    List<String> routeIdValidate = new ArrayList<>();
    for (RouteDefinition routeDefinition : routes) {
      ValidateUtil.validate(routeDefinition);
      String routeId = routeDefinition.getId();
      if (!routeIdValidate.contains(routeId)) {
        routeIdValidate.add(routeId);
      } else {
        throw new GatewayCustomException(StrUtil.format("Duplicate with routeId of {}.", routeId));
      }
    }
  }

  /**
   * Sort routes.
   *
   * @param routes routes
   * @return sorted routes
   */
  default List<RouteDefinition> sortRoutes(List<RouteDefinition> routes) {
    Assert.notEmpty(routes, "Need sort routes cannot empty.");
    return routes.stream()
        .sorted(Comparator.comparingInt(RouteDefinition::getOrder))
        .collect(Collectors.toList());
  }
}
