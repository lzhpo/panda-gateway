package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.core.loadbalancer.RouteLoadBalancer;
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
  public WebfluxForwardFilter webfluxForwardFilter(
      WebClient webClient, RouteLoadBalancer routeLoadBalancer) {
    return new WebfluxForwardFilter(webClient, routeLoadBalancer);
  }
}
