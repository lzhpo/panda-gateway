package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.actuator.GatewayControllerEndpoint;
import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.handler.GatewayRequestHandler;
import com.lzhpo.panda.gateway.handler.GatewayRequestMapping;
import com.lzhpo.panda.gateway.route.DefaultRouteInitializer;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
import com.lzhpo.panda.gateway.route.RouteLocator;
import com.lzhpo.panda.gateway.support.ClientIpResolver;
import com.lzhpo.panda.gateway.support.GatewayErrorAttributes;
import com.lzhpo.panda.gateway.support.GatewayErrorWebExceptionHandler;
import com.lzhpo.panda.gateway.support.KeyResolver;
import com.lzhpo.panda.gateway.support.RedisRateLimiter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.ViewResolver;
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
  @Primary
  public GatewayErrorAttributes gatewayErrorAttributes() {
    return new GatewayErrorAttributes();
  }

  /** Reference: {@link ErrorWebFluxAutoConfiguration#errorWebExceptionHandler} */
  @Bean
  @Order(-2)
  public GatewayErrorWebExceptionHandler gatewayErrorWebExceptionHandler(
      WebProperties webProperties,
      ErrorAttributes errorAttributes,
      ServerProperties serverProperties,
      ApplicationContext applicationContext,
      ObjectProvider<ViewResolver> viewResolvers,
      ServerCodecConfigurer serverCodecConfigurer) {
    GatewayErrorWebExceptionHandler exceptionHandler =
        new GatewayErrorWebExceptionHandler(
            errorAttributes,
            webProperties.getResources(),
            serverProperties.getError(),
            applicationContext);
    exceptionHandler.setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()));
    exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
    exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
    return exceptionHandler;
  }

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
}
