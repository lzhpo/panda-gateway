package com.lzhpo.panda.gateway.servlet.predicate;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.servlet.enums.ConfigTypeEnum;
import java.util.Collections;
import java.util.List;

/**
 * @author lzhpo
 */
public interface RoutePredicateFactory<T> {

  ServletPredicate apply(T t);

  T getConfig(RouteDefinition route);

  default List<String> configFieldNames() {
    return Collections.emptyList();
  }

  default ConfigTypeEnum configTypeEnum() {
    return ConfigTypeEnum.STRING;
  }

  default String getName() {
    return getClass().getSimpleName().replace(RoutePredicateFactory.class.getSimpleName(), "");
  }
}
