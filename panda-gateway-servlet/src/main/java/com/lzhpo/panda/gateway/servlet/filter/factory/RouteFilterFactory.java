package com.lzhpo.panda.gateway.servlet.filter.factory;

import com.lzhpo.panda.gateway.core.config.ConfigFactory;
import com.lzhpo.panda.gateway.servlet.filter.RouteFilter;

/**
 * @author lzhpo
 */
public interface RouteFilterFactory<T> extends ConfigFactory<T> {

  RouteFilter filter(T config);

  @Override
  default String currentName() {
    return getClass().getSimpleName().replace(RouteFilterFactory.class.getSimpleName(), "");
  }
}
