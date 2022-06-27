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
