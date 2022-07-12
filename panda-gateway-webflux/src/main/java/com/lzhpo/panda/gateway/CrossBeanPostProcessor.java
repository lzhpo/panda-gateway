/*
 * Copyright 2022 lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.reactive.handler.AbstractHandlerMapping;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class CrossBeanPostProcessor implements BeanPostProcessor {

  private final GatewayProperties gatewayProperties;

  @Override
  public Object postProcessAfterInitialization(Object bean, @NonNull String beanName)
      throws BeansException {
    if (AbstractHandlerMapping.class.isAssignableFrom(bean.getClass())) {
      AbstractHandlerMapping handlerMapping = (AbstractHandlerMapping) bean;
      handlerMapping.setCorsConfigurations(gatewayProperties.getCrossConfigurations());
      return handlerMapping;
    }
    return bean;
  }
}
