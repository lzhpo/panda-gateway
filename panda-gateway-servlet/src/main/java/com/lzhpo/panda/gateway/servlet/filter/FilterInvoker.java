package com.lzhpo.panda.gateway.servlet.filter;

import com.lzhpo.panda.gateway.servlet.filter.chain.FilterInvokerChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzhpo
 */
public interface FilterInvoker {

  void doFilter(HttpServletRequest request, HttpServletResponse response, FilterInvokerChain chain);
}
