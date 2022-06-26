package com.lzhpo.panda.gateway.servlet.predicate.factory;

import com.lzhpo.panda.gateway.core.config.ConfigFactory;

/**
 * Just extends it if you want implements route predicate.
 *
 * @author lzhpo
 */
public abstract class AbstractRoutePredicateFactory<T>
    implements ConfigFactory<T>, RoutePredicateFactory<T> {

  protected final Class<T> configClass;

  protected AbstractRoutePredicateFactory(Class<T> configClass) {
    this.configClass = configClass;
  }

  @Override
  public Class<T> getConfigClass() {
    return configClass;
  }
}
