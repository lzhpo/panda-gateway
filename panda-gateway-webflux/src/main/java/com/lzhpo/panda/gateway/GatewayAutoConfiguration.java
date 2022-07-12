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

import com.lzhpo.panda.gateway.actuator.GatewayControllerEndpoint;
import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.handler.GatewayRequestHandler;
import com.lzhpo.panda.gateway.handler.GatewayRequestMapping;
import com.lzhpo.panda.gateway.route.DefaultRouteInitializer;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
import com.lzhpo.panda.gateway.route.RouteLocator;
import com.lzhpo.panda.gateway.support.ClientIpResolver;
import com.lzhpo.panda.gateway.support.KeyResolver;
import com.lzhpo.panda.gateway.support.RedisRateLimiter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class GatewayAutoConfiguration {

  private final RouteLocator routeLocator;
  private final RouteDefinitionLocator routeDefinitionLocator;

  @Bean
  public DefaultRouteInitializer defaultRouteInitializer() {
    return new DefaultRouteInitializer(routeDefinitionLocator);
  }

  @Bean
  public KeyResolver clientIpKeyResolver() {
    return exchange -> Mono.just(clientIpResolver().resolve(exchange));
  }

  @Bean
  @ConditionalOnClass({RedisScript.class, ReactiveStringRedisTemplate.class})
  @ConditionalOnProperty(prefix = "gateway.redis", value = "enabled", havingValue = "true")
  public RedisRateLimiter redisRateLimiter(
      RedisScript<List<Long>> rateLimitRedisScript,
      ReactiveStringRedisTemplate stringRedisTemplate) {
    return new RedisRateLimiter(rateLimitRedisScript, stringRedisTemplate);
  }

  @Bean
  public ClientIpResolver clientIpResolver() {
    return serverWebExchange -> {
      ServerHttpRequest request = serverWebExchange.getRequest();
      return Optional.ofNullable(request.getRemoteAddress())
          .map(InetSocketAddress::getAddress)
          .map(InetAddress::getHostAddress)
          .orElse(null);
    };
  }

  @Bean
  public GatewayControllerEndpoint gatewayControllerEndpoint() {
    return new GatewayControllerEndpoint(routeDefinitionLocator);
  }

  @Bean
  public GatewayRequestHandler gatewayRequestHandler(WebClient.Builder webClientBuilder) {
    return new GatewayRequestHandler(routeLocator, webClientBuilder);
  }

  @Bean
  public GatewayRequestMapping gatewayRequestMapping(
      GatewayProperties gatewayProperties, GatewayRequestHandler requestHandler) {
    return new GatewayRequestMapping(routeLocator, gatewayProperties, requestHandler);
  }

  @Bean
  public CrossBeanPostProcessor crossBeanPostProcessor(GatewayProperties gatewayProperties) {
    return new CrossBeanPostProcessor(gatewayProperties);
  }
}
