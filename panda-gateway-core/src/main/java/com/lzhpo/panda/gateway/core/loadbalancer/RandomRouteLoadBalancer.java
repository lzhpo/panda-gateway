package com.lzhpo.panda.gateway.core.loadbalancer;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.util.AntPathMatcher;

/**
 * Random to choose route.
 *
 * @author lzhpo
 */
@RequiredArgsConstructor
public class RandomRouteLoadBalancer implements RouteLoadBalancer {

  private final GatewayProperties gatewayProperties;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Override
  public RouteDefinition choose(String requestPath) {
    return gatewayProperties.getRoutes().stream()
        .filter(proxyRoute -> antPathMatcher.match(proxyRoute.getMatchPath(), requestPath))
        .findAny()
        .orElse(null);
  }
}
