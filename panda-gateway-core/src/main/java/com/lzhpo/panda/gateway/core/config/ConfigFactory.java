package com.lzhpo.panda.gateway.core.config;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.BeanUtils;

/**
 * @author lzhpo
 */
public interface ConfigFactory<T> {

  String currentName();

  default T newConfigInstance(Class<T> configClass) {
    return BeanUtils.instantiateClass(configClass);
  }

  T parseToConfig(RouteDefinition route);

  default List<String> configFieldOrder() {
    return Collections.emptyList();
  }

  default ConfigTypeEnum configFieldType() {
    return ConfigTypeEnum.DEFAULT;
  }
}
