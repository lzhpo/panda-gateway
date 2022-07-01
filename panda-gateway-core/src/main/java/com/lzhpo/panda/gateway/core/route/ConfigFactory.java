package com.lzhpo.panda.gateway.core.route;

import cn.hutool.core.bean.BeanUtil;
import com.lzhpo.panda.gateway.core.exception.GatewayCustomException;
import com.lzhpo.panda.gateway.core.utils.ValidateUtil;
import java.util.Map;
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
      config = BeanUtil.toBean(args, configClass);
    } catch (Exception e) {
      throw new GatewayCustomException(
          String.format("[%s] args configuration is wrong.", currentComponentName), e);
    }

    ValidateUtil.validate(
        config, errorMsg -> String.format("[%s] %s", currentComponentName, errorMsg));
    return config;
  }
}
