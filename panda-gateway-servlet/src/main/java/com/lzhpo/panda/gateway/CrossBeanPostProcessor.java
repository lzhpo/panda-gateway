package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class CrossBeanPostProcessor implements BeanPostProcessor {

  private final GatewayProperties gatewayProperties;

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    if (AbstractHandlerMapping.class.isAssignableFrom(bean.getClass())) {
      AbstractHandlerMapping handlerMapping = (AbstractHandlerMapping) bean;
      handlerMapping.setCorsConfigurations(gatewayProperties.getCrossConfigurations());
      return handlerMapping;
    }
    return bean;
  }
}
