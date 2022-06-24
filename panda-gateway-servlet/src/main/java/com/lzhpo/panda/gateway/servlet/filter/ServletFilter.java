package com.lzhpo.panda.gateway.servlet.filter;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzhpo
 */
public interface ServletFilter {

  void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain,
      RouteDefinition route);

  String getPrefix();

  default String getSuffix() {
    return ServletFilter.class.getSimpleName();
  }
}
