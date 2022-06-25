package com.lzhpo.panda.gateway.servlet.filter.support;

import com.lzhpo.panda.gateway.servlet.filter.GlobalServletFilter;
import com.lzhpo.panda.gateway.servlet.filter.ServletFilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzhpo
 */
@Slf4j
public class LogGlobalServletFilter implements GlobalServletFilter {

  @Override
  public void filter(
      HttpServletRequest request, HttpServletResponse response, ServletFilterChain filterChain) {
    log.info("Request [{}]", request.getRequestURI());
    filterChain.doFilter(request, response);
  }
}
