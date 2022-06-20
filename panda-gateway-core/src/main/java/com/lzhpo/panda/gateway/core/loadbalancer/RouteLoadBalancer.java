package com.lzhpo.panda.gateway.core.loadbalancer;

import com.lzhpo.panda.gateway.core.RouteDefinition;

/**
 * @author lzhpo
 */
public interface RouteLoadBalancer {

  /**
   * According to request path to choose route.
   *
   * @param requestPath requestPath
   * @return choose route
   */
  RouteDefinition choose(String requestPath);
}
