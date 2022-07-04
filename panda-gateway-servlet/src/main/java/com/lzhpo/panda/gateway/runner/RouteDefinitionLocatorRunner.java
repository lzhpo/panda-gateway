package com.lzhpo.panda.gateway.runner;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.util.CollectionUtils;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class RouteDefinitionLocatorRunner implements CommandLineRunner {

  private final GatewayProperties gatewayProperties;
  private final RouteDefinitionLocator routeDefinitionLocator;

  @Override
  public void run(String... args) {
    List<RouteDefinition> routes = gatewayProperties.getRoutes();
    if (!CollectionUtils.isEmpty(routes)) {
      routes.forEach(routeDefinitionLocator::saveRoute);
    }
  }
}
