package com.lzhpo.panda.gateway.core;

import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class RestTemplateBeanPostProcessor implements BeanPostProcessor {

  private final ApplicationContext context;
  private final GatewayProperties gatewayProperties;
  private final LoadBalancerInterceptor loadBalancerInterceptor;

  @Override
  public Object postProcessBeforeInitialization(@Nonnull Object bean, @Nonnull String beanName)
      throws BeansException {
    if (bean instanceof RestTemplate) {
      LoadBalanced loadBalanced = context.findAnnotationOnBean(beanName, LoadBalanced.class);
      if (Objects.nonNull(loadBalanced) || gatewayProperties.isDiscovery()) {
        RestTemplate restTemplate = (RestTemplate) bean;
        restTemplate.getInterceptors().add(loadBalancerInterceptor);
        log.info("Enabled loadBalance for restTemplate.");
        return restTemplate;
      }
    }
    return bean;
  }
}
