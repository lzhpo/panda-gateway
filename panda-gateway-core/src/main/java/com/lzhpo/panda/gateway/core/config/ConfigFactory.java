package com.lzhpo.panda.gateway.core.config;

import cn.hutool.core.util.ReflectUtil;
import com.lzhpo.panda.gateway.core.ComponentDefinition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * @author lzhpo
 */
public interface ConfigFactory<T> {

  Class<T> getConfigClass();

  String currentName();

  default T newConfigInstance(Class<T> configClass) {
    return BeanUtils.instantiateClass(configClass);
  }

  default List<String> configFieldOrder() {
    return Collections.emptyList();
  }

  default ConfigTypeEnum configFieldType() {
    return ConfigTypeEnum.DEFAULT;
  }

  default T getConfig(ComponentDefinition componentDefinition) {
    if (ObjectUtils.isEmpty(componentDefinition)) {
      return null;
    }

    Class<T> configClass = getConfigClass();
    T config = newConfigInstance(configClass);
    List<String> fieldNames = configFieldOrder();
    Map<String, String> args = componentDefinition.getArgs();

    ConfigTypeEnum configType = configFieldType();
    switch (configType) {
      case DEFAULT:
        args.forEach(
            (name, arg) -> {
              String fieldName = fieldNames.get(Integer.parseInt(name));
              ReflectUtil.setFieldValue(config, fieldName, arg);
            });
        break;
      case LIST:
        Assert.isTrue(fieldNames.size() == 1, "Config type of LIST should be 1 field order.");
        List<String> values = new ArrayList<>(args.values());
        ReflectUtil.setFieldValue(config, fieldNames.get(0), values);
        break;
      case MAP:
      default:
        throw new UnsupportedOperationException("Not support type " + configType.name());
    }

    return config;
  }
}
