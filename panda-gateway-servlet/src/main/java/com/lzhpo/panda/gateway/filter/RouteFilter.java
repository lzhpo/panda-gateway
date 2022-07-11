package com.lzhpo.panda.gateway.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Route filter just apply current route.
 *
 * @author lzhpo
 */
public interface RouteFilter {

  /**
   * Execute route filter.
   *
   * @param request {@link HttpServletRequest}
   * @param response {@link HttpServletResponse}
   * @param chain {@link RouteFilterChain}
   */
  void doFilter(HttpServletRequest request, HttpServletResponse response, RouteFilterChain chain);
}
