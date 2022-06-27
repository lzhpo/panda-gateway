package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.webflux.filter.GlobalFilter;
import com.lzhpo.panda.gateway.webflux.filter.factory.StripPrefixRouteFilterFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.AfterRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.BeforeRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.BetweenRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.ClientIpRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.CookieRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.HeaderRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.MethodRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.PathRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.QueryRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.WeightRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.support.ClientIpResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

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
  public AfterRoutePredicateFactory afterRoutePredicateFactory() {
    return new AfterRoutePredicateFactory();
  }

  @Bean
  public BeforeRoutePredicateFactory beforeRoutePredicateFactory() {
    return new BeforeRoutePredicateFactory();
  }

  @Bean
  public BetweenRoutePredicateFactory betweenRoutePredicateFactory() {
    return new BetweenRoutePredicateFactory();
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

  @Bean
  public ClientIpRoutePredicateFactory clientIpRoutePredicateFactory(
      ClientIpResolver clientIpResolver) {
    return new ClientIpRoutePredicateFactory(clientIpResolver);
  }

  @Bean
  public CookieRoutePredicateFactory cookieRoutePredicateFactory() {
    return new CookieRoutePredicateFactory();
  }

  @Bean
  public HeaderRoutePredicateFactory headerRoutePredicateFactory() {
    return new HeaderRoutePredicateFactory();
  }

  @Bean
  public MethodRoutePredicateFactory methodRoutePredicateFactory() {
    return new MethodRoutePredicateFactory();
  }

  @Bean
  public PathRoutePredicateFactory pathRoutePredicateFactory() {
    return new PathRoutePredicateFactory();
  }

  @Bean
  public QueryRoutePredicateFactory queryRoutePredicateFactory() {
    return new QueryRoutePredicateFactory();
  }

  @Bean
  public WeightRoutePredicateFactory weightRoutePredicateFactory(
      @Lazy RouteDefinitionLocator routeDefinitionLocator) {
    return new WeightRoutePredicateFactory(routeDefinitionLocator);
  }
}
