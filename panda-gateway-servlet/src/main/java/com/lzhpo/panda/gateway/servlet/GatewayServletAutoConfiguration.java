package com.lzhpo.panda.gateway.servlet;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.servlet.filter.GlobalServletFilter;
import com.lzhpo.panda.gateway.servlet.filter.ServletFilter;
import com.lzhpo.panda.gateway.servlet.filter.ServletWebFilter;
import com.lzhpo.panda.gateway.servlet.filter.support.LogGlobalServletFilter;
import com.lzhpo.panda.gateway.servlet.filter.support.StripPrefixServletFilter;
import com.lzhpo.panda.gateway.servlet.predicate.PathRoutePredicateFactory;
import com.lzhpo.panda.gateway.servlet.predicate.RoutePredicateFactory;
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

  // ====== filters ======

  @Bean
  public StripPrefixServletFilter stripPrefixServletFilter() {
    return new StripPrefixServletFilter();
  }

  @Bean
  public LogGlobalServletFilter globalServletFilter() {
    return new LogGlobalServletFilter();
  }

  // ====== predicates ======

  @Bean
  public PathRoutePredicateFactory pathServletPredicate() {
    return new PathRoutePredicateFactory();
  }

  // =========================

  @Bean
  public RouteComponentLocator routeComponentLocator(
      List<RoutePredicateFactory> predicateFactories,
      List<ServletFilter> filters,
      List<GlobalServletFilter> globalFilters) {
    return new RouteComponentLocator(predicateFactories, filters, globalFilters);
  }

  @Bean
  @ConditionalOnMissingBean
  public RouteDefinitionLocator routeDefinitionLocator(GatewayProperties gatewayProperties) {
    return new DefaultCacheRouteDefinitionLocator(gatewayProperties.getRoutes());
  }

  @Bean
  public ServletWebFilter servletWebFilter(
      RestTemplate restTemplate,
      GatewayProperties gatewayProperties,
      RouteComponentLocator routeComponentLocator) {
    return new ServletWebFilter(restTemplate, gatewayProperties, routeComponentLocator);
  }
}
