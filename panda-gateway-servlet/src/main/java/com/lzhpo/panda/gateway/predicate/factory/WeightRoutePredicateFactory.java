package com.lzhpo.panda.gateway.predicate.factory;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.lang.WeightRandom.WeightObj;
import com.lzhpo.panda.gateway.RouteDefinitionLocator;
import com.lzhpo.panda.gateway.core.ComponentDefinition;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.config.ConfigTypeEnum;
import com.lzhpo.panda.gateway.core.consts.GatewayConst;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
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
  public List<String> configFieldOrder() {
    return ListUtil.of("group", "weight");
  }

  @Override
  public ConfigTypeEnum configFieldType() {
    return ConfigTypeEnum.DEFAULT;
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      RouteDefinition currentRoute =
          (RouteDefinition) request.getAttribute(GatewayConst.ROUTE_DEFINITION);
      String currentRouteId = currentRoute.getId();
      String group = config.getGroup();
      WeightRandom<RouteDefinition> routeWeightRandom = new WeightRandom<>();

      List<RouteDefinition> routes = routeDefinitionLocator.getRoutes();
      routes.forEach(
          route -> {
            List<ComponentDefinition> predicateDefinitions = route.getPredicates();
            predicateDefinitions.forEach(
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

    @Min(0)
    @Max(10)
    private double weight;
  }
}
