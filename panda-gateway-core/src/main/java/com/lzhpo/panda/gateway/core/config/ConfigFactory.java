package com.lzhpo.panda.gateway.core.config;

import cn.hutool.core.bean.BeanUtil;
import com.lzhpo.panda.gateway.core.ComponentDefinition;
import com.lzhpo.panda.gateway.core.GatewayCustomException;
import com.lzhpo.panda.gateway.core.ValidateUtil;
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
    Assert.notNull(componentDefinition, "componentDefinition cannot null.");
    Class<T> configClass = getConfigClass();
    Map<String, String> args = componentDefinition.getArgs();

    T config;
    try {
      config = BeanUtil.toBean(args, configClass);
    } catch (Exception e) {
      throw new GatewayCustomException("args configuration is wrong", e);
    }

    ValidateUtil.validate(config);
    return config;
  }
}
