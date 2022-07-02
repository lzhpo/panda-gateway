package com.lzhpo.panda.gateway.core.route;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.lzhpo.panda.gateway.core.ComponentConstructorArgs;
import com.lzhpo.panda.gateway.core.exception.GatewayCustomException;
import com.lzhpo.panda.gateway.core.utils.ValidateUtil;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

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
   * To new config instance.
   *
   * @return config instance
   */
  default T newConfigInstance() {
    Class<T> configClass = getConfigClass();
    return BeanUtils.instantiateClass(configClass);
  }

  /**
   * Use {@code componentDefinition} to create config.
   *
   * @param componentDefinition {@link ComponentDefinition}
   * @return created config
   */
  default T getConfig(ComponentDefinition componentDefinition) {
    String currentComponentName = getClass().getSimpleName();

    Assert.notNull(
        componentDefinition,
        String.format("[%s] componentDefinition cannot null.", currentComponentName));
    Class<T> configClass = getConfigClass();
    Map<String, Object> args = componentDefinition.getArgs();
    T config;

    try {
      Constructor<T> constructor =
          Arrays.stream(ReflectUtil.getConstructors(configClass))
              .filter(x -> AnnotationUtil.hasAnnotation(x, ComponentConstructorArgs.class))
              .findAny()
              .orElse(null);

      if (Objects.nonNull(constructor)) {
        config = constructor.newInstance(args);
      } else {
        config = BeanUtil.toBean(args, configClass);
      }
    } catch (Exception e) {
      throw new GatewayCustomException(
          String.format("[%s] args configuration is wrong.", currentComponentName), e);
    }

    ValidateUtil.validate(
        config, errorMsg -> String.format("[%s] %s", currentComponentName, errorMsg));
    return config;
  }
}
