package com.lzhpo.panda.gateway.core.config;

import cn.hutool.core.util.ReflectUtil;
import com.lzhpo.panda.gateway.core.ComponentDefinition;
import com.lzhpo.panda.gateway.core.GatewayCustomException;
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

  default T getConfig(List<ComponentDefinition> componentDefinitions) {
    if (ObjectUtils.isEmpty(componentDefinitions)) {
      return null;
    }

    Class<T> configClass = getConfigClass();
    T config = newConfigInstance(configClass);
    List<String> fieldNames = configFieldOrder();
    String currentPredicateName = currentName();

    ConfigTypeEnum configType = configFieldType();
    switch (configType) {
      case DEFAULT:
        invokeDefault(componentDefinitions, config, fieldNames, currentPredicateName);
        break;
      case LIST:
        invokeList(componentDefinitions, config, fieldNames, currentPredicateName);
        break;
      case MAP:
      default:
        throw new UnsupportedOperationException("Not support type " + configType.name());
    }

    return config;
  }

  private void invokeDefault(
      List<ComponentDefinition> componentDefinitions,
      T config,
      List<String> fieldNames,
      String currentPredicateName) {

    componentDefinitions.stream()
        .filter(predicate -> predicate.getName().equalsIgnoreCase(currentPredicateName))
        .findAny()
        .ifPresentOrElse(
            predicate -> {
              Map<String, String> args = predicate.getArgs();
              args.forEach(
                  (name, arg) -> {
                    String fieldName = fieldNames.get(Integer.parseInt(name));
                    ReflectUtil.setFieldValue(config, fieldName, arg);
                  });
            },
            () -> {
              throw new GatewayCustomException("Not found config.");
            });
  }

  private void invokeList(
      List<ComponentDefinition> componentDefinitions,
      T config,
      List<String> fieldNames,
      String currentPredicateName) {

    Assert.isTrue(fieldNames.size() == 1, "Config type of LIST should be 1 field order.");
    componentDefinitions.stream()
        .filter(predicate -> predicate.getName().equalsIgnoreCase(currentPredicateName))
        .findAny()
        .ifPresentOrElse(
            predicate -> {
              Map<String, String> args = predicate.getArgs();
              List<String> values = new ArrayList<>(args.values());
              ReflectUtil.setFieldValue(config, fieldNames.get(0), values);
            },
            () -> {
              throw new GatewayCustomException("Not found config.");
            });
  }
}
