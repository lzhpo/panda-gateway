package com.lzhpo.panda.gateway.core.config;

import cn.hutool.core.util.ReflectUtil;
import com.lzhpo.panda.gateway.core.ComponentDefinition;
import com.lzhpo.panda.gateway.core.ValidateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * @author lzhpo
 */
public interface ConfigFactory<T> {

  /**
   * Get config class
   *
   * @return config class
   */
  Class<T> getConfigClass();

  /**
   * Get current component name.
   *
   * @return current component name
   */
  String currentName();

  /**
   * Use {@code configClass} to new config instance.
   *
   * @param configClass configClass
   * @return config instance
   */
  default T newConfigInstance(Class<T> configClass) {
    return BeanUtils.instantiateClass(configClass);
  }

  /**
   * Have order of config fields.
   *
   * @return config fields
   */
  List<String> configFieldOrder();

  /**
   * Config type
   *
   * @return {@link ConfigTypeEnum}
   */
  ConfigTypeEnum configFieldType();

  /**
   * Use {@code componentDefinition} to create config.
   *
   * @param componentDefinition {@link ComponentDefinition}
   * @return created config
   */
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

    ValidateUtil.validate(config);
    return config;
  }
}
