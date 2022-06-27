package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.webflux.filter.GlobalFilter;
import com.lzhpo.panda.gateway.webflux.filter.factory.StripPrefixRouteFilterFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.PathRoutePredicateFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author lzhpo
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(DispatcherHandler.class)
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class GatewayWebfluxAutoConfiguration {

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
  public StripPrefixRouteFilterFactory stripPrefixRouteFilterFactory() {
    return new StripPrefixRouteFilterFactory();
  }

  @Bean
  public PathRoutePredicateFactory pathRoutePredicateFactory() {
    return new PathRoutePredicateFactory();
  }
}
