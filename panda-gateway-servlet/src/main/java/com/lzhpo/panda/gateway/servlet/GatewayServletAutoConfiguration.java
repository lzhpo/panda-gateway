package com.lzhpo.panda.gateway.servlet;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.servlet.filter.ServletWebFilter;
import com.lzhpo.panda.gateway.servlet.filter.factory.FilterFactory;
import com.lzhpo.panda.gateway.servlet.filter.factory.StripPrefixFilterFactory;
import com.lzhpo.panda.gateway.servlet.filter.global.GlobalFilterInvoker;
import com.lzhpo.panda.gateway.servlet.predicate.PathPredicateFactory;
import com.lzhpo.panda.gateway.servlet.predicate.PredicateInvoker;
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
  public StripPrefixFilterFactory stripPrefixFilterFactory() {
    return new StripPrefixFilterFactory();
  }

  // ====== predicates ======

  @Bean
  public PathPredicateFactory pathPredicateFactory() {
    return new PathPredicateFactory();
  }

  // =========================

  @Bean
  public RouteComponentLocator routeComponentLocator(
      List<PredicateInvoker> predicateFactories,
      List<FilterFactory> filters,
      List<GlobalFilterInvoker> globalFilters) {
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
      RouteDefinitionLocator routeDefinitionLocator,
      RouteComponentLocator routeComponentLocator) {
    return new ServletWebFilter(restTemplate, routeDefinitionLocator, routeComponentLocator);
  }
}
