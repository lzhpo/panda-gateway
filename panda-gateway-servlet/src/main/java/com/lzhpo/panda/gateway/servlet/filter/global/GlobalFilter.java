package com.lzhpo.panda.gateway.servlet.filter.global;

import com.lzhpo.panda.gateway.servlet.filter.chain.RouteFilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Global filter.
 *
 * @author lzhpo
 */
public interface GlobalFilter {

  /**
   * Execute global filter.
   *
   * @param request {@link HttpServletRequest}
   * @param response {@link HttpServletResponse}
   * @param chain {@link RouteFilterChain}
   */
  void filter(HttpServletRequest request, HttpServletResponse response, RouteFilterChain chain);
}
