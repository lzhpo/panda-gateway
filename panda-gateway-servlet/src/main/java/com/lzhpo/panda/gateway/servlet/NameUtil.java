package com.lzhpo.panda.gateway.servlet;

import com.lzhpo.panda.gateway.servlet.filter.factory.FilterFactory;
import com.lzhpo.panda.gateway.servlet.predicate.PredicateFactory;
import lombok.experimental.UtilityClass;

/**
 * @author lzhpo
 */
@UtilityClass
public class NameUtil {

  public static String normalizePredicateName(Class<PredicateFactory<?>> clazz) {
    return clazz.getSimpleName().replace(PredicateFactory.class.getSimpleName(), "");
  }

  public static String normalizeFilterName(Class<FilterFactory> clazz) {
    return clazz.getSimpleName().replace(FilterFactory.class.getSimpleName(), "");
  }
}
