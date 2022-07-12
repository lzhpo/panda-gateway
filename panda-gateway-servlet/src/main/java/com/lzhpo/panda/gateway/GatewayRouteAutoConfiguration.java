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

import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.route.CacheRouteLocator;
import com.lzhpo.panda.gateway.route.DefaultRouteInitializer;
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
  public DefaultRouteInitializer defaultRouteInitializer(
      RouteDefinitionLocator routeDefinitionLocator) {
    return new DefaultRouteInitializer(routeDefinitionLocator);
  }

  @Bean
  public CacheRouteLocator cacheRouteLocator(RouteDefinitionLocator routeDefinitionLocator) {
    return new CacheRouteLocator(routeDefinitionLocator);
  }

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
