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

package com.lzhpo.panda.gateway.core;

import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class WebClientBuilderBeanPostProcessor implements BeanPostProcessor {

  private final ApplicationContext context;
  private final GatewayProperties gatewayProperties;
  private final DeferringLoadBalancerExchangeFilterFunction<LoadBalancedExchangeFilterFunction>
      exchangeFilterFunction;

  @Override
  public Object postProcessBeforeInitialization(@Nonnull Object bean, @Nonnull String beanName)
      throws BeansException {
    if (bean instanceof WebClient.Builder) {
      LoadBalanced loadBalanced = context.findAnnotationOnBean(beanName, LoadBalanced.class);
      if (Objects.nonNull(loadBalanced) || gatewayProperties.isDiscovery()) {
        ((WebClient.Builder) bean).filter(exchangeFilterFunction);
        log.info("Enabled loadBalance for WebClient.Builder.");
      }
    }
    return bean;
  }
}
