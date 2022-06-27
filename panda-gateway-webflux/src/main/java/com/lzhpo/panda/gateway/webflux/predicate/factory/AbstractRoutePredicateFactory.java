package com.lzhpo.panda.gateway.webflux.predicate.factory;

import com.lzhpo.panda.gateway.core.route.ConfigFactory;

/**
 * Just extends it if you want implements route predicate.
 *
 * @author lzhpo
 */
public abstract class AbstractRoutePredicateFactory<T>
    implements ConfigFactory<T>, RoutePredicateFactory<T> {

  protected final Class<T> configClass;

  public AbstractRoutePredicateFactory(Class<T> configClass) {
    this.configClass = configClass;
  }

  @Override
  public Class<T> getConfigClass() {
    return configClass;
  }
}
