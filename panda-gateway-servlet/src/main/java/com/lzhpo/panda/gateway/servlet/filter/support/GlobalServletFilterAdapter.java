package com.lzhpo.panda.gateway.servlet.filter.support;

import com.lzhpo.panda.gateway.servlet.filter.GlobalServletFilter;
import com.lzhpo.panda.gateway.servlet.filter.ServletFilter;
import com.lzhpo.panda.gateway.servlet.filter.ServletFilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class GlobalServletFilterAdapter implements ServletFilter {

  private final GlobalServletFilter globalServletFilter;

  @Override
  public void filter(
      HttpServletRequest request, HttpServletResponse response, ServletFilterChain filterChain) {
    globalServletFilter.filter(request, response, filterChain);
  }
}
