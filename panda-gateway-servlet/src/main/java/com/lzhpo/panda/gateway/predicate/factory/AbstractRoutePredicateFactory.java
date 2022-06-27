package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.core.ComponentDefinition;
import com.lzhpo.panda.gateway.core.config.ConfigFactory;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Just extends it if you want implements route predicate.
 *
 * @author lzhpo
 */
public abstract class AbstractRoutePredicateFactory<T>
    implements ConfigFactory<T>, RoutePredicateFactory<T> {

  protected final Class<T> configClass;
  private final Map<String, RoutePredicate> routePredicates = new HashMap<>();

  public AbstractRoutePredicateFactory(Class<T> configClass) {
    this.configClass = configClass;
  }

  @Override
  public Class<T> getConfigClass() {
    return configClass;
  }

  @Override
  public RoutePredicate create(ComponentDefinition predicateDefinition) {
    String predicateName = predicateDefinition.getName();
    return Optional.ofNullable(routePredicates.get(predicateName))
        .orElseGet(
            () -> {
              RoutePredicate routePredicate =
                  RoutePredicateFactory.super.create(predicateDefinition);
              routePredicates.put(predicateName, routePredicate);
              return routePredicate;
            });
  }
}
