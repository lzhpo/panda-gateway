package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.ConfigFactory;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;

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

  /**
   * Get current route predicate name.
   *
   * @return current route predicate name
   */
  @Override
  default String currentName() {
    return getClass().getSimpleName().replace(RoutePredicateFactory.class.getSimpleName(), "");
  }
}
