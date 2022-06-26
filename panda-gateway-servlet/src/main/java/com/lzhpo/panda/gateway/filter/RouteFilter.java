package com.lzhpo.panda.gateway.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Just for route filter.
 *
 * @author lzhpo
 */
public interface RouteFilter {

  /**
   * Execute route filter chain.
   *
   * @param request {@link HttpServletRequest}
   * @param response {@link HttpServletResponse}
   * @param chain {@link RouteFilterChain}
   */
  void doFilter(HttpServletRequest request, HttpServletResponse response, RouteFilterChain chain);
}
