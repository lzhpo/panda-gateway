package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.actuator.GatewayControllerEndpoint;
import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.filter.GlobalFilter;
import com.lzhpo.panda.gateway.support.ClientIpResolver;
import com.lzhpo.panda.gateway.support.KeyResolver;
import com.lzhpo.panda.gateway.support.RedisRateLimiter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
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
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class GatewayWebfluxAutoConfiguration {

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
  public GatewayControllerEndpoint gatewayControllerEndpoint(
      RouteDefinitionLocator routeDefinitionLocator) {
    return new GatewayControllerEndpoint(routeDefinitionLocator);
  }

  @Bean
  public GatewayRequestHandler gatewayRequestHandler(
      WebClient.Builder webClientBuilder, RouteDefinitionLocator routeDefinitionLocator) {
    return new GatewayRequestHandler(webClientBuilder, routeDefinitionLocator);
  }

  @Bean
  public GatewayRequestMapping gatewayRequestMapping(
      GatewayRequestHandler webfluxWebHandler, RouteDefinitionLocator routeDefinitionLocator) {
    return new GatewayRequestMapping(webfluxWebHandler, routeDefinitionLocator);
  }

  @Bean
  public RouteDefinitionLocator routeDefinitionLocator(
      GatewayProperties gatewayProperties, List<GlobalFilter> globalFilters) {
    return new MemoryRouteDefinitionLocator(gatewayProperties.getRoutes(), globalFilters);
  }
}
