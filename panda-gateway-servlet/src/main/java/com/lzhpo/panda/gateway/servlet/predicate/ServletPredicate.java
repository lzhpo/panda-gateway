package com.lzhpo.panda.gateway.servlet.predicate;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import javax.servlet.http.HttpServletRequest;

/**
 * @author lzhpo
 */
public interface ServletPredicate {

  boolean apply(HttpServletRequest request, RouteDefinition route);

  String getPrefix();

  default String getSuffix() {
    return ServletPredicate.class.getSimpleName();
  }
}
