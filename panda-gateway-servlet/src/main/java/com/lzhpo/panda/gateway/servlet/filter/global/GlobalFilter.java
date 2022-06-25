package com.lzhpo.panda.gateway.servlet.filter.global;

import com.lzhpo.panda.gateway.servlet.filter.chain.RouteFilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzhpo
 */
public interface GlobalFilter {

  void filter(
      HttpServletRequest request, HttpServletResponse response, RouteFilterChain filterChain);
}
