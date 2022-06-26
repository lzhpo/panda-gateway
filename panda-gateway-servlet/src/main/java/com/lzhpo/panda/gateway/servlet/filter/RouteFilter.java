package com.lzhpo.panda.gateway.servlet.filter;

import com.lzhpo.panda.gateway.servlet.filter.chain.RouteFilterChain;
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
