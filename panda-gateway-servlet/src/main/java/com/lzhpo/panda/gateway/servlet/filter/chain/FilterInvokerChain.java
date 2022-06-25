package com.lzhpo.panda.gateway.servlet.filter.chain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzhpo
 */
public interface FilterInvokerChain {

  void doFilter(HttpServletRequest request, HttpServletResponse response);
}
