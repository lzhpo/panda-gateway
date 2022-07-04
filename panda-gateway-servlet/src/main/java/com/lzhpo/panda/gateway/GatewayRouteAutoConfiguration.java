package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.route.MemoryRouteDefinitionLocator;
import com.lzhpo.panda.gateway.route.RedisRouteDefinitionLocator;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author lzhpo
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
public class GatewayRouteAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnClass({RedisTemplate.class})
  @ConditionalOnProperty(prefix = "gateway.redis", value = "route-locator", havingValue = "true")
  public RouteDefinitionLocator redisRouteDefinitionLocator(
      RedisTemplate<String, RouteDefinition> redisTemplate) {
    return new RedisRouteDefinitionLocator(redisTemplate);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(prefix = "gateway.redis", value = "route-locator", havingValue = "false")
  public RouteDefinitionLocator memoryRouteDefinitionLocator() {
    return new MemoryRouteDefinitionLocator();
  }
}
