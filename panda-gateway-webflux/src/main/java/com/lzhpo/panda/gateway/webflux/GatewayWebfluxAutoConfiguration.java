package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.webflux.actuator.GatewayControllerEndpoint;
import com.lzhpo.panda.gateway.webflux.filter.GlobalFilter;
import com.lzhpo.panda.gateway.webflux.support.ClientIpResolver;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author lzhpo
 */
@Configuration
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class GatewayWebfluxAutoConfiguration {

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

  @Bean
  public ClientIpResolver clientIpResolver() {
    return new ClientIpResolver() {
      @Override
      public String resolve(ServerWebExchange serverWebExchange) {
        return ClientIpResolver.super.resolve(serverWebExchange);
      }
    };
  }
}
