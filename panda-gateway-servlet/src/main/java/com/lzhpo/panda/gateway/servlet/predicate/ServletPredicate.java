package com.lzhpo.panda.gateway.servlet.predicate;

import com.lzhpo.panda.gateway.core.Route;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzhpo
 */
public interface ServletPredicate {

  boolean apply(HttpServletRequest request, HttpServletResponse response, Route route);

  String getPrefix();

  default String getSuffix() {
    return ServletPredicate.class.getSimpleName();
  }
}
