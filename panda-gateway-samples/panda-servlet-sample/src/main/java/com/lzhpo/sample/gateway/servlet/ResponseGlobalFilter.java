package com.lzhpo.sample.gateway.servlet;

import com.lzhpo.panda.gateway.filter.GlobalFilter;
import com.lzhpo.panda.gateway.filter.RouteFilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * @author lzhpo
 */
@Component
public class ResponseGlobalFilter implements GlobalFilter {

  @Override
  public void filter(
      HttpServletRequest request, HttpServletResponse response, RouteFilterChain chain) {
    response.addHeader("country", "China");
    response.addHeader("city", "Guangzhou");
    chain.doFilter(request, response);
  }
}
