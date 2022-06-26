package com.lzhpo.panda.gateway.servlet.filter.factory;

import com.lzhpo.panda.gateway.core.ComponentDefinition;
import com.lzhpo.panda.gateway.core.config.ConfigFactory;
import com.lzhpo.panda.gateway.servlet.filter.RouteFilter;

/**
 * @author lzhpo
 */
public interface RouteFilterFactory<T> extends ConfigFactory<T> {

  RouteFilter filter(T config);

  default RouteFilter apply(ComponentDefinition componentDefinition) {
    T config = getConfig(componentDefinition);
    return filter(config);
  }

  @Override
  default String currentName() {
    return getClass().getSimpleName().replace(RouteFilterFactory.class.getSimpleName(), "");
  }
}
