package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.filter.GlobalFilter;
import com.lzhpo.panda.gateway.filter.WebRequestFilter;
import com.lzhpo.panda.gateway.filter.factory.StripPrefixRouteFilterFactory;
import com.lzhpo.panda.gateway.predicate.factory.PathRoutePredicateFactory;
import java.util.List;
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
  public PathRoutePredicateFactory pathPredicateFactory() {
    return new PathRoutePredicateFactory();
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