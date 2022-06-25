package com.lzhpo.panda.gateway.servlet.filter;

import com.lzhpo.panda.gateway.servlet.filter.chain.RouteFilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzhpo
 */
public interface RouteFilter {

  void doFilter(HttpServletRequest request, HttpServletResponse response, RouteFilterChain chain);
}
