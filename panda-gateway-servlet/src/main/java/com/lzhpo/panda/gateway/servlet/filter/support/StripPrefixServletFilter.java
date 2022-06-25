package com.lzhpo.panda.gateway.servlet.filter.support;

import com.lzhpo.panda.gateway.core.ExtractUtils;
import com.lzhpo.panda.gateway.servlet.filter.ServletFilter;
import com.lzhpo.panda.gateway.servlet.filter.ServletFilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;

/**
 * @author lzhpo
 */
public class StripPrefixServletFilter implements ServletFilter, Ordered {

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  @Override
  public void filter(
      HttpServletRequest request, HttpServletResponse response, ServletFilterChain filterChain) {
    filterChain.doFilter(newRequest(request), response);
  }

  private HttpServletRequest newRequest(HttpServletRequest request) {
    return new HttpServletRequestWrapper(request) {

      @Override
      public String getRequestURI() {
        String requestUri = super.getRequestURI();
        return ExtractUtils.stripPrefix(requestUri, 2);
      }
    };
  }
}
