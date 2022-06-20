package com.lzhpo.panda.gateway.servlet;

import com.lzhpo.panda.gateway.core.GatewayProperties;
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
  public ServletForwardFilter servletForwardFilter(
      GatewayProperties gatewayProperties, RestTemplate restTemplate) {
    return new ServletForwardFilter(restTemplate, gatewayProperties);
  }
}
