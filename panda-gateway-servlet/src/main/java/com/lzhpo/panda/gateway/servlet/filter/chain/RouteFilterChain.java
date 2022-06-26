package com.lzhpo.panda.gateway.servlet.filter.chain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter chain for route.
 *
 * @author lzhpo
 */
public interface RouteFilterChain {

  /**
   * Execute next filter of this filter chain.
   *
   * @param request {@link HttpServletRequest}
   * @param response {@link HttpServletResponse}
   */
  void doFilter(HttpServletRequest request, HttpServletResponse response);
}
