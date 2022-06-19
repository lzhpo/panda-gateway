package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author lzhpo
 */
@Configuration
public class GatewayWebfluxAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public WebClient webClient() {
    return WebClient.create();
  }

  @Bean
  public WebClientForwardFilter webClientForwardFilter(
      WebClient webClient, GatewayProperties gatewayProperties) {
    return new WebClientForwardFilter(webClient, gatewayProperties);
  }
}
