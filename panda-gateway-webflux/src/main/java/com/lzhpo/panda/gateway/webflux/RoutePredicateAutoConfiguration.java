package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.webflux.predicate.factory.AfterRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.BeforeRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.BetweenRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.ClientIpRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.CookieRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.HeaderRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.MethodRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.ParameterRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.PathRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.predicate.factory.WeightRoutePredicateFactory;
import com.lzhpo.panda.gateway.webflux.support.ClientIpResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author lzhpo
 */
@Configuration
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class RoutePredicateAutoConfiguration {

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
  public ParameterRoutePredicateFactory parameterRoutePredicateFactory() {
    return new ParameterRoutePredicateFactory();
  }

  @Bean
  public WeightRoutePredicateFactory weightRoutePredicateFactory(
      @Lazy RouteDefinitionLocator routeDefinitionLocator) {
    return new WeightRoutePredicateFactory(routeDefinitionLocator);
  }
}
