package com.lzhpo.panda.gateway.route;

import cn.hutool.core.util.ArrayUtil;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.route.RouteInitializer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class DefaultRouteInitializer implements RouteInitializer {

  private final RouteDefinitionLocator routeDefinitionLocator;

  @Override
  public void initialize(List<RouteDefinition> routeDefinitions) {
    if (!CollectionUtils.isEmpty(routeDefinitions)) {
      routeDefinitionLocator
          .saveRoutes(ArrayUtil.toArray(routeDefinitions, RouteDefinition.class))
          .subscribe();
    }
  }
}
