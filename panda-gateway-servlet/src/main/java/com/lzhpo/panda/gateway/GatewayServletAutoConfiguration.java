package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.filter.GlobalFilter;
import com.lzhpo.panda.gateway.filter.WebRequestFilter;
import com.lzhpo.panda.gateway.filter.factory.StripPrefixRouteFilterFactory;
import com.lzhpo.panda.gateway.predicate.factory.AfterRoutePredicateFactory;
import com.lzhpo.panda.gateway.predicate.factory.BeforeRoutePredicateFactory;
import com.lzhpo.panda.gateway.predicate.factory.BetweenRoutePredicateFactory;
import com.lzhpo.panda.gateway.predicate.factory.ClientIpRoutePredicateFactory;
import com.lzhpo.panda.gateway.predicate.factory.CookieRoutePredicateFactory;
import com.lzhpo.panda.gateway.predicate.factory.HeaderRoutePredicateFactory;
import com.lzhpo.panda.gateway.predicate.factory.MethodRoutePredicateFactory;
import com.lzhpo.panda.gateway.predicate.factory.PathRoutePredicateFactory;
import com.lzhpo.panda.gateway.predicate.factory.QueryRoutePredicateFactory;
import com.lzhpo.panda.gateway.support.ClientIpResolver;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author lzhpo
 */
@Configuration
public class GatewayServletAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public StripPrefixRouteFilterFactory stripPrefixFilterFactory() {
    return new StripPrefixRouteFilterFactory();
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
  public PathRoutePredicateFactory pathPredicateFactory() {
    return new PathRoutePredicateFactory();
  }

  @Bean
  public QueryRoutePredicateFactory queryRoutePredicateFactory() {
    return new QueryRoutePredicateFactory();
  }

  @Bean
  @ConditionalOnMissingBean
  public ClientIpResolver clientIpResolver() {
    return new ClientIpResolver() {
      @Override
      public String resolve(HttpServletRequest request) {
        return ClientIpResolver.super.resolve(request);
      }
    };
  }

  @Bean
  public ClientIpRoutePredicateFactory clientIpRoutePredicateFactory(
      ClientIpResolver clientIpResolver) {
    return new ClientIpRoutePredicateFactory(clientIpResolver);
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
  @ConditionalOnMissingBean
  public RouteDefinitionLocator routeDefinitionLocator(
      GatewayProperties gatewayProperties, List<GlobalFilter> globalFilters) {
    return new MemoryRouteDefinitionLocator(gatewayProperties.getRoutes(), globalFilters);
  }

  @Bean
  public WebRequestFilter servletWebFilter(
      RestTemplate restTemplate, RouteDefinitionLocator routeDefinitionLocator) {
    return new WebRequestFilter(restTemplate, routeDefinitionLocator);
  }
}
