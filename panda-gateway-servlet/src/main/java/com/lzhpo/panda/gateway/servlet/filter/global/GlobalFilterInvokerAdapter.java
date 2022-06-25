package com.lzhpo.panda.gateway.servlet.filter.global;

import com.lzhpo.panda.gateway.servlet.filter.FilterInvoker;
import com.lzhpo.panda.gateway.servlet.filter.chain.FilterInvokerChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class GlobalFilterInvokerAdapter implements FilterInvoker {

  private final GlobalFilterInvoker globalServletFilter;

  @Override
  public void doFilter(
      HttpServletRequest request, HttpServletResponse response, FilterInvokerChain chain) {
    globalServletFilter.filter(request, response, chain);
  }
}
