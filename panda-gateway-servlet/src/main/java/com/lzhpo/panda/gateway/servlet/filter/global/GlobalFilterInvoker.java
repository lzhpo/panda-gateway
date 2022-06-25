package com.lzhpo.panda.gateway.servlet.filter.global;

import com.lzhpo.panda.gateway.servlet.filter.chain.FilterInvokerChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzhpo
 */
public interface GlobalFilterInvoker {

  void filter(
      HttpServletRequest request, HttpServletResponse response, FilterInvokerChain filterChain);
}
