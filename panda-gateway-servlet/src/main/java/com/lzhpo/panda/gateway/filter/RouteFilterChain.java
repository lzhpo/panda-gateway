package com.lzhpo.panda.gateway.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter chain for route.
 *
 * @author lzhpo
 */
public interface RouteFilterChain {

  /**
   * Execute the next filter of this filter chain.
   *
   * @param request {@link HttpServletRequest}
   * @param response {@link HttpServletResponse}
   */
  void doFilter(HttpServletRequest request, HttpServletResponse response);
}
