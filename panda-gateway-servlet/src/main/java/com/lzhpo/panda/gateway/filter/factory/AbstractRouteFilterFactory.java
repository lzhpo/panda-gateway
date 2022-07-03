package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.core.route.ConfigFactory;

/**
 * Just extends it if you want implements route filter.
 *
 * @author lzhpo
 */
public abstract class AbstractRouteFilterFactory<T>
    implements ConfigFactory<T>, RouteFilterFactory<T> {

  protected final Class<T> configClass;

  protected AbstractRouteFilterFactory(Class<T> configClass) {
    this.configClass = configClass;
  }

  @Override
  public Class<T> getConfigClass() {
    return configClass;
  }
}
