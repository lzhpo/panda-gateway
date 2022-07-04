package com.lzhpo.panda.gateway.route;

import com.lzhpo.panda.gateway.core.exception.GatewayCustomException;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.utils.ValidateUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
public interface RouteDefinitionLocator {

  /**
   * According to routeId to get route.
   *
   * @param routeId routeId
   * @return route
   */
  Mono<RouteDefinition> getRoute(String routeId);

  /**
   * To get routes.
   *
   * @return routes
   */
  Flux<RouteDefinition> getRoutes();

  /**
   * Save route
   *
   * @param route route
   * @return Void
   */
  Mono<Void> saveRoute(RouteDefinition route);

  /**
   * Delete route
   *
   * @param routeId routeId
   * @return Void
   */
  Mono<Void> deleteRoute(String routeId);

  /**
   * Validate route
   *
   * @param routes routes
   * @return Void
   */
  default Mono<Void> validateRoute(List<RouteDefinition> routes) {
    Assert.notEmpty(routes, "routes cannot empty.");
    List<String> routeIdValidate = new ArrayList<>();
    for (RouteDefinition routeDefinition : routes) {
      ValidateUtil.validate(routeDefinition);
      String routeId = routeDefinition.getId();
      if (!routeIdValidate.contains(routeId)) {
        routeIdValidate.add(routeId);
      } else {
        throw new GatewayCustomException("Duplicate with routeId of " + routeId);
      }
    }
    return Mono.empty();
  }

  /**
   * Sort routes.
   *
   * @param routes routes
   * @return sorted routes
   */
  default Flux<RouteDefinition> sortRoutes(List<RouteDefinition> routes) {
    Assert.notEmpty(routes, "Need sort routes cannot empty.");
    return Flux.fromArray(
        routes.stream()
            .sorted(Comparator.comparingInt(RouteDefinition::getOrder))
            .toArray(RouteDefinition[]::new));
  }
}
