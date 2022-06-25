package com.lzhpo.panda.gateway.servlet.filter.global;

import com.lzhpo.panda.gateway.servlet.filter.RouteFilter;
import com.lzhpo.panda.gateway.servlet.filter.chain.RouteFilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class GlobalFilterAdapter implements RouteFilter {

  private final GlobalFilter globalServletFilter;

  @Override
  public void doFilter(
      HttpServletRequest request, HttpServletResponse response, RouteFilterChain chain) {
    globalServletFilter.filter(request, response, chain);
  }
}
