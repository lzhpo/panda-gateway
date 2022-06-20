package com.lzhpo.panda.gateway.core;

import com.lzhpo.panda.gateway.core.loadbalancer.RandomRouteLoadBalancer;
import com.lzhpo.panda.gateway.core.loadbalancer.RouteLoadBalancer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzhpo
 */
@Configuration
@EnableConfigurationProperties({GatewayProperties.class})
public class GatewayAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public RouteLoadBalancer routeLoadBalancer(GatewayProperties gatewayProperties) {
    return new RandomRouteLoadBalancer(gatewayProperties);
  }
}
