package com.lzhpo.panda.gateway.servlet.filter.factory;

import com.lzhpo.panda.gateway.core.config.ConfigFactory;
import com.lzhpo.panda.gateway.servlet.filter.FilterInvoker;

/**
 * @author lzhpo
 */
public interface FilterFactory<T> extends ConfigFactory<T> {

  FilterInvoker filter(T config);

  @Override
  default String currentName() {
    return getClass().getSimpleName().replace(FilterFactory.class.getSimpleName(), "");
  }
}
