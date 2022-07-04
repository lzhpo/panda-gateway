package com.lzhpo.panda.gateway.actuator;

import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
@ControllerEndpoint(id = "gateway")
public class GatewayControllerEndpoint {

  private final RouteDefinitionLocator routeDefinitionLocator;

  @GetMapping("/routes")
  public ResponseEntity<List<RouteDefinition>> routes() {
    return ResponseEntity.ok(routeDefinitionLocator.getRoutes());
  }

  @GetMapping("/routes/{id}")
  public ResponseEntity<RouteDefinition> route(@PathVariable String id) {
    return ResponseEntity.ok(routeDefinitionLocator.getRoute(id));
  }
}
