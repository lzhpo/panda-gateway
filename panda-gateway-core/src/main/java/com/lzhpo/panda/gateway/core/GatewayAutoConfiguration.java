package com.lzhpo.panda.gateway.core;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * At the same time, support {@code gateway.discovery=true} and mark {@link LoadBalanced} to control
 * enable loadBalance.
 *
 * <pre>{@code
 * @Bean
 * @LoadBalanced
 * @ConditionalOnMissingBean
 * public RestTemplate restTemplate() {
 *   return new RestTemplate();
 * }
 * }</pre>
 *
 * <pre>{@code
 * @Bean
 * @LoadBalanced
 * @ConditionalOnMissingBean
 * public WebClient.Builder webClientBuilder() {
 *   return WebClient.builder();
 * }
 * }</pre>
 *
 * @author lzhpo
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({GatewayProperties.class})
public class GatewayAutoConfiguration {

  private final GatewayProperties gatewayProperties;
  private final ApplicationContext applicationContext;

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnWebApplication(type = Type.SERVLET)
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnWebApplication(type = Type.REACTIVE)
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  @Bean
  @ConditionalOnWebApplication(type = Type.SERVLET)
  public RestTemplateBeanPostProcessor restTemplateBeanPostProcessor(
      LoadBalancerInterceptor loadBalancerInterceptor) {
    return new RestTemplateBeanPostProcessor(
        applicationContext, gatewayProperties, loadBalancerInterceptor);
  }

  @Bean
  @ConditionalOnWebApplication(type = Type.REACTIVE)
  public WebClientBuilderBeanPostProcessor webClientBuilderBeanPostProcessor(
      DeferringLoadBalancerExchangeFilterFunction<LoadBalancedExchangeFilterFunction>
          exchangeFilterFunction) {
    return new WebClientBuilderBeanPostProcessor(
        applicationContext, gatewayProperties, exchangeFilterFunction);
  }
}
