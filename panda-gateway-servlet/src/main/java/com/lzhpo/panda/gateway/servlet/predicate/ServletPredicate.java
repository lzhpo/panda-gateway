package com.lzhpo.panda.gateway.servlet.predicate;

import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;

/**
 * @author lzhpo
 */
public interface ServletPredicate extends Predicate<HttpServletRequest> {

  default String getName() {
    return getClass().getSimpleName().replace(ServletPredicate.class.getSimpleName(), "");
  }
}
