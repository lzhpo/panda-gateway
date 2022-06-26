package com.lzhpo.panda.gateway.servlet.predicate.factory;

import com.lzhpo.panda.gateway.core.ComponentDefinition;
import com.lzhpo.panda.gateway.core.config.ConfigFactory;
import com.lzhpo.panda.gateway.servlet.predicate.RoutePredicate;

/**
 * Route predicate factory, it can create route predicate.
 *
 * @author lzhpo
 */
public interface RoutePredicateFactory<T> extends ConfigFactory<T> {

  /**
   * Use {@code config} to create route predicate.
   *
   * @param config config
   * @return created route predicate
   */
  RoutePredicate create(T config);

  /**
   * Use {@code predicateDefinition} to create route predicate.
   *
   * @param predicateDefinition predicate definition
   * @return created route predicate
   */
  default RoutePredicate create(ComponentDefinition predicateDefinition) {
    T config = getConfig(predicateDefinition);
    return create(config);
  }

  @Override
  default String currentName() {
    return getClass().getSimpleName().replace(RoutePredicateFactory.class.getSimpleName(), "");
  }
}
