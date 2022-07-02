package com.lzhpo.panda.gateway.predicate.factory;

import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.lang.WeightRandom.WeightObj;
import com.lzhpo.panda.gateway.RouteDefinitionLocator;
import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by weight.
 *
 * @author lzhpo
 */
@Slf4j
public class WeightRoutePredicateFactory
    extends AbstractRoutePredicateFactory<WeightRoutePredicateFactory.Config> {

  private final RouteDefinitionLocator routeDefinitionLocator;

  public WeightRoutePredicateFactory(RouteDefinitionLocator routeDefinitionLocator) {
    super(Config.class);
    this.routeDefinitionLocator = routeDefinitionLocator;
  }

  @Override
  public RoutePredicate create(Config config) {
    return serverWebExchange -> {
      RouteDefinition currentRoute = serverWebExchange.getAttribute(GatewayConst.ROUTE_DEFINITION);
      if (Objects.isNull(currentRoute)) {
        return false;
      }

      String currentRouteId = currentRoute.getId();
      String group = config.getGroup();
      WeightRandom<RouteDefinition> routeWeightRandom = new WeightRandom<>();

      List<RouteDefinition> routes = routeDefinitionLocator.getRoutes();
      routes.forEach(
          route -> {
            List<ComponentDefinition> predicateDefinitions = route.getPredicates();
            predicateDefinitions.stream()
                .filter(predicateDefinition -> predicateDefinition.getName().equals(currentName()))
                .forEach(
                    predicateDefinition -> {
                      Config weightConfig = getConfig(predicateDefinition);
                      if (weightConfig.getGroup().equals(group)) {
                        routeWeightRandom.add(new WeightObj<>(route, weightConfig.getWeight()));
                      }
                    });
          });

      RouteDefinition randomRoute = routeWeightRandom.next();
      return randomRoute.getId().equals(currentRouteId);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotBlank private String group;

    private double weight;
  }
}
