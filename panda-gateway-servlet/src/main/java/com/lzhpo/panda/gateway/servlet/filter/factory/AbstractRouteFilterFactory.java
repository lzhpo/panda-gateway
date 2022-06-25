package com.lzhpo.panda.gateway.servlet.filter.factory;

import com.lzhpo.panda.gateway.core.config.ConfigFactory;

/**
 * @author lzhpo
 */
public abstract class AbstractRouteFilterFactory<T>
    implements ConfigFactory<T>, RouteFilterFactory<T> {

  protected final Class<T> configClass;

  public AbstractRouteFilterFactory(Class<T> configClass) {
    this.configClass = configClass;
  }

  @Override
  public Class<T> getConfigClass() {
    return configClass;
  }
}
