package com.lzhpo.panda.gateway;

import cn.hutool.extra.servlet.ServletUtil;
import com.lzhpo.panda.gateway.actuator.GatewayControllerEndpoint;
import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.handler.GatewayRequestMapping;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
import com.lzhpo.panda.gateway.runner.RouteDefinitionLocatorRunner;
import com.lzhpo.panda.gateway.support.ClientIpResolver;
import com.lzhpo.panda.gateway.support.KeyResolver;
import com.lzhpo.panda.gateway.support.RedisRateLimiter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.client.RestTemplate;

/**
 * @author lzhpo
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = Type.SERVLET)
public class GatewayAutoConfiguration {

  private final RouteDefinitionLocator routeDefinitionLocator;

  @Bean
  public RouteDefinitionLocatorRunner routeRunner(GatewayProperties gatewayProperties) {
    return new RouteDefinitionLocatorRunner(gatewayProperties, routeDefinitionLocator);
  }

  @Bean
  public GatewayControllerEndpoint gatewayControllerEndpoint() {
    return new GatewayControllerEndpoint(routeDefinitionLocator);
  }

  @Bean
  public KeyResolver clientIpKeyResolver() {
    return ServletUtil::getClientIP;
  }

  @Bean
  @ConditionalOnClass({RedisScript.class, StringRedisTemplate.class})
  @ConditionalOnProperty(prefix = "gateway.redis", value = "enabled", havingValue = "true")
  public RedisRateLimiter redisRateLimiter(
      RedisScript<List<Long>> rateLimitRedisScript, StringRedisTemplate stringRedisTemplate) {
    return new RedisRateLimiter(rateLimitRedisScript, stringRedisTemplate);
  }

  @Bean
  @ConditionalOnMissingBean
  public ClientIpResolver clientIpResolver() {
    return ServletUtil::getClientIP;
  }

  @Bean
  public GatewayRequestMapping servletWebFilter(RestTemplate restTemplate) {
    return new GatewayRequestMapping(restTemplate, routeDefinitionLocator);
  }
}
