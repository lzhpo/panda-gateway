package com.lzhpo.panda.gateway.webflux.actuator;

import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.webflux.RouteDefinitionLocator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
@ControllerEndpoint(id = "gateway")
public class GatewayControllerEndpoint {

  private final RouteDefinitionLocator routeDefinitionLocator;

  @GetMapping("/routes")
  public Mono<ResponseEntity<List<RouteDefinition>>> routes() {
    return Mono.just(ResponseEntity.ok(routeDefinitionLocator.getRoutes()));
  }

  @GetMapping("/routes/{id}")
  public Mono<ResponseEntity<RouteDefinition>> route(@PathVariable String id) {
    return Mono.just(ResponseEntity.ok(routeDefinitionLocator.getRoute(id)));
  }
}
