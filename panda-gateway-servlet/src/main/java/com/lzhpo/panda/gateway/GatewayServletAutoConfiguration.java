package com.lzhpo.panda.gateway;

import cn.hutool.extra.servlet.ServletUtil;
import com.lzhpo.panda.gateway.actuator.GatewayControllerEndpoint;
import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.route.MemoryRouteDefinitionLocator;
import com.lzhpo.panda.gateway.route.RedisRouteDefinitionLocator;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
import com.lzhpo.panda.gateway.support.ClientIpResolver;
import com.lzhpo.panda.gateway.support.KeyResolver;
import com.lzhpo.panda.gateway.support.RedisRateLimiter;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author lzhpo
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
public class GatewayServletAutoConfiguration {

  @PostConstruct
  public void initRoutes(RouteDefinitionLocator routeLocator, GatewayProperties properties) {
    List<RouteDefinition> routes = properties.getRoutes();
    if (!CollectionUtils.isEmpty(routes)) {
      routes.forEach(routeLocator::saveRoute);
    }
  }

  @Bean
  public GatewayControllerEndpoint gatewayControllerEndpoint(
      RouteDefinitionLocator routeDefinitionLocator) {
    return new GatewayControllerEndpoint(routeDefinitionLocator);
  }

  @Bean
  public KeyResolver clientIpKeyResolver() {
    return ServletUtil::getClientIP;
  }

  @Bean
  @ConditionalOnClass({RedisTemplate.class})
  @ConditionalOnProperty(prefix = "gateway.redis", value = "route-locator", havingValue = "true")
  public RouteDefinitionLocator routeDefinitionLocator(
      RedisTemplate<String, RouteDefinition> redisTemplate) {
    return new RedisRouteDefinitionLocator(redisTemplate);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(prefix = "gateway.redis", value = "route-locator", havingValue = "false")
  public RouteDefinitionLocator routeDefinitionLocator() {
    return new MemoryRouteDefinitionLocator();
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
  public GatewayRequestMapping servletWebFilter(
      RestTemplate restTemplate, RouteDefinitionLocator routeDefinitionLocator) {
    return new GatewayRequestMapping(restTemplate, routeDefinitionLocator);
  }
}
