package com.lzhpo.gateway.servlet;

import com.lzhpo.gateway.core.GatewayProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author lzhpo
 */
@Configuration
public class GatewayServletAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public GatewayServletProxyFilter gatewayServletProxyFilter(
      GatewayProperties gatewayProperties, RestTemplate restTemplate) {
    return new GatewayServletProxyFilter(restTemplate, gatewayProperties);
  }
}
