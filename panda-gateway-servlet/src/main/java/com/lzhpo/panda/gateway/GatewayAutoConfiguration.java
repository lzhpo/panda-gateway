/*
 * Copyright 2022 lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzhpo.panda.gateway;

import cn.hutool.extra.servlet.ServletUtil;
import com.lzhpo.panda.gateway.actuator.GatewayControllerEndpoint;
import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.handler.GatewayRequestMapping;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
import com.lzhpo.panda.gateway.route.RouteLocator;
import com.lzhpo.panda.gateway.support.ClientIpResolver;
import com.lzhpo.panda.gateway.support.KeyResolver;
import com.lzhpo.panda.gateway.support.RedisRateLimiter;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author lzhpo
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = Type.SERVLET)
public class GatewayAutoConfiguration {

  private final GatewayProperties gatewayProperties;

  @Bean
  public GatewayRequestMapping gatewayRequestMapping(
      ServletContext servletContext, RouteLocator routeLocator, RestTemplate restTemplate) {
    return new GatewayRequestMapping(servletContext, routeLocator, restTemplate, gatewayProperties);
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
  @ConditionalOnExpression(
      "!T(org.springframework.util.ObjectUtils).isEmpty('${gateway.cross-configurations}')")
  public CrossBeanPostProcessor crossBeanPostProcessor() {
    return new CrossBeanPostProcessor(gatewayProperties);
  }

  @Bean
  public CrossWebMvcConfiguration crossWebMvcConfiguration() {
    return new CrossWebMvcConfiguration(gatewayProperties);
  }

  /** Cross-domain configuration */
  public static class CrossWebMvcConfiguration extends WebMvcConfigurationSupport {

    public CrossWebMvcConfiguration(GatewayProperties gatewayProperties) {
      Map<String, CorsConfiguration> corsConfigurations =
          gatewayProperties.getCrossConfigurations();
      if (!ObjectUtils.isEmpty(corsConfigurations)) {
        Map<String, CorsConfiguration> finalCorsConfigurations = getCorsConfigurations();
        finalCorsConfigurations.putAll(corsConfigurations);
      }
    }
  }
}
