package com.lzhpo.panda.gateway.servlet.predicate.factory;

import com.lzhpo.panda.gateway.core.config.ConfigFactory;
import com.lzhpo.panda.gateway.servlet.predicate.RoutePredicate;

/**
 * @author lzhpo
 */
public interface RoutePredicateFactory<T> extends ConfigFactory<T> {

  RoutePredicate invoke(T config);

  @Override
  default String currentName() {
    return getClass().getSimpleName().replace(RoutePredicateFactory.class.getSimpleName(), "");
  }
}
