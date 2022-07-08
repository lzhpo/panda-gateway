package com.lzhpo.panda.gateway.actuator;

import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.route.RouteRefreshEvent;
import com.lzhpo.panda.gateway.filter.factory.RouteFilterFactory;
import com.lzhpo.panda.gateway.predicate.factory.RoutePredicateFactory;
import com.lzhpo.panda.gateway.route.RouteComponentExtractor;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

  @GetMapping("/routes/predicates")
  public ResponseEntity<List<String>> predicates() {
    String[] predicateNames = SpringUtil.getBeanNamesForType(RoutePredicateFactory.class);
    return ResponseEntity.ok(
        Arrays.stream(predicateNames)
            .map(predicateName -> SpringUtil.getBean(predicateName, RoutePredicateFactory.class))
            .map(RoutePredicateFactory::getClass)
            .map(Class::toString)
            .collect(Collectors.toList()));
  }

  @GetMapping("/routes/{routeId}/predicates")
  public ResponseEntity<List<String>> predicates(@PathVariable String routeId) {
    List<String> predicateNames =
        getComponentDefinitionNames(
            routeId,
            RouteDefinition::getPredicates,
            componentDefinition ->
                RouteComponentExtractor.getPredicateFactory(componentDefinition.getName())
                    .getClass()
                    .getName());
    return ResponseEntity.ok(predicateNames);
  }

  @GetMapping("/routes/filters")
  public ResponseEntity<List<String>> filters() {
    String[] filterNames = SpringUtil.getBeanNamesForType(RouteFilterFactory.class);
    return ResponseEntity.ok(
        Arrays.stream(filterNames)
            .map(filterName -> SpringUtil.getBean(filterName, RouteFilterFactory.class))
            .map(RouteFilterFactory::getClass)
            .map(Class::toString)
            .collect(Collectors.toList()));
  }

  @GetMapping("/routes/{routeId}/filters")
  public ResponseEntity<List<String>> filters(@PathVariable String routeId) {
    List<String> filterNames =
        getComponentDefinitionNames(
            routeId,
            RouteDefinition::getFilters,
            componentDefinition ->
                RouteComponentExtractor.getFilterFactory(componentDefinition.getName())
                    .getClass()
                    .getName());
    return ResponseEntity.ok(filterNames);
  }

  @PostMapping("/routes/refresh")
  public ResponseEntity<Boolean> refresh() {
    SpringUtil.publishEvent(new RouteRefreshEvent(this));
    return ResponseEntity.ok(true);
  }

  private List<String> getComponentDefinitionNames(
      String routeId,
      Function<RouteDefinition, List<ComponentDefinition>> routeMapper,
      Function<ComponentDefinition, String> componentMapper) {
    return routeDefinitionLocator.getRoutes().stream()
        .filter(routeDefinition -> routeDefinition.getId().equals(routeId))
        .findAny()
        .map(routeMapper)
        .map(
            componentDefinitions ->
                componentDefinitions.stream().map(componentMapper).collect(Collectors.toList()))
        .orElse(Collections.emptyList());
  }
}
