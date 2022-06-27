package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.ConfigFactory;
import com.lzhpo.panda.gateway.filter.RouteFilter;

/**
 * Route filter factory, it can create route filter.
 *
 * @author lzhpo
 */
public interface RouteFilterFactory<T> extends ConfigFactory<T> {

  /**
   * Use {@code config} to create route filter.
   *
   * @param config config
   * @return created route filter
   */
  RouteFilter create(T config);

  /**
   * Use {@code componentDefinition} to create route filter.
   *
   * @param componentDefinition componentDefinition
   * @return created route filter
   */
  default RouteFilter create(ComponentDefinition componentDefinition) {
    T config = getConfig(componentDefinition);
    return create(config);
  }

  /**
   * Get current route filter name.
   *
   * @return current route filter name
   */
  @Override
  default String currentName() {
    return getClass().getSimpleName().replace(RouteFilterFactory.class.getSimpleName(), "");
  }
}
