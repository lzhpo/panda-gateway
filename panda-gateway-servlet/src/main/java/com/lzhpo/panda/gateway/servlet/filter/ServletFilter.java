package com.lzhpo.panda.gateway.servlet.filter;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import javax.servlet.http.HttpServletRequest;

/**
 * @author lzhpo
 */
public interface ServletFilter {

  HttpServletRequest doFilterInternal(HttpServletRequest request, RouteDefinition route);

  String getPrefix();

  default String getSuffix() {
    return ServletFilter.class.getSimpleName();
  }
}
