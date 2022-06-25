package com.lzhpo.panda.gateway.servlet.predicate;

import com.lzhpo.panda.gateway.core.config.ConfigFactory;

/**
 * @author lzhpo
 */
public interface PredicateFactory<T> extends ConfigFactory<T> {

  @Override
  default String currentName() {
    return getClass().getSimpleName().replace(PredicateFactory.class.getSimpleName(), "");
  }
}
