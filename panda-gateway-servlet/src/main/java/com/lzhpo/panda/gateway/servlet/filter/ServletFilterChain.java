package com.lzhpo.panda.gateway.servlet.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzhpo
 */
public interface ServletFilterChain {

  void doFilter(HttpServletRequest request, HttpServletResponse response);
}
