package com.lzhpo.panda.gateway.servlet.predicate;

import com.lzhpo.panda.gateway.core.Route;
import com.lzhpo.panda.gateway.core.RouteUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzhpo
 */
public class PathServletPredicate implements ServletPredicate {

  @Override
  public String getPrefix() {
    String suffix = ServletPredicate.super.getSuffix();
    return getClass().getSimpleName().replace(suffix, "");
  }

  @Override
  public boolean apply(HttpServletRequest request, HttpServletResponse response, Route route) {
    String prefix = getPrefix();
    String requestPath = request.getRequestURI();
    return RouteUtil.isMatch(route, prefix, requestPath);
  }
}
