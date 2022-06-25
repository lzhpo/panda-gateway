package com.lzhpo.panda.gateway.servlet.filter;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.consts.GatewayConst;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzhpo
 */
public interface ServletFilter {

  void filter(
      HttpServletRequest request, HttpServletResponse response, ServletFilterChain filterChain);

  default String getName() {
    return getClass().getSimpleName().replace(ServletFilter.class.getSimpleName(), "");
  }

  default Optional<RouteDefinition> getRoute(HttpServletRequest request) {
    return Optional.ofNullable(request.getAttribute(GatewayConst.ROUTE_DEFINITION))
        .map(RouteDefinition.class::cast);
  }
}
