package com.lzhpo.panda.gateway.actuator;

import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.route.RouteRefreshEvent;
import com.lzhpo.panda.gateway.route.Route;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
import com.lzhpo.panda.gateway.route.RouteLocator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
@ControllerEndpoint(id = "gateway")
public class GatewayControllerEndpoint {

  private final RouteLocator routeLocator;
  private final RouteDefinitionLocator routeDefinitionLocator;

  @GetMapping("/routes")
  public ResponseEntity<Flux<RouteDefinition>> routes() {
    return ResponseEntity.ok(routeDefinitionLocator.getRoutes());
  }

  @GetMapping("/routes/{id}")
  public ResponseEntity<Mono<RouteDefinition>> route(@PathVariable String id) {
    return ResponseEntity.ok(routeDefinitionLocator.getRoute(id));
  }

  @GetMapping("/routes/predicates")
  public ResponseEntity<Flux<String>> predicates() {
    return ResponseEntity.ok(
        Flux.fromArray(
            routeLocator.getRoutes().stream()
                .map(Route::getPredicates)
                .map(Object::getClass)
                .map(Class::getName)
                .toArray(String[]::new)));
  }

  @GetMapping("/routes/filters")
  public ResponseEntity<Flux<String>> filters() {
    return ResponseEntity.ok(
        Flux.fromArray(
            routeLocator.getRoutes().stream()
                .map(Route::getFilters)
                .map(Object::getClass)
                .map(Class::getName)
                .toArray(String[]::new)));
  }

  @PostMapping("/routes/refresh")
  public ResponseEntity<Mono<Boolean>> refresh() {
    SpringUtil.publishEvent(new RouteRefreshEvent(this));
    return ResponseEntity.ok(Mono.just(true));
  }
}
