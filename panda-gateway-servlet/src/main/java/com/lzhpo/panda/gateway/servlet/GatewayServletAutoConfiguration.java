package com.lzhpo.panda.gateway.servlet;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.servlet.filter.GlobalServletFilter;
import com.lzhpo.panda.gateway.servlet.filter.ServletWebFilter;
import com.lzhpo.panda.gateway.servlet.filter.support.LogGlobalServletFilter;
import com.lzhpo.panda.gateway.servlet.filter.support.StripPrefixServletFilter;
import com.lzhpo.panda.gateway.servlet.predicate.PathServletPredicate;
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
  public ServletWebFilter servletWebFilter(
      RestTemplate restTemplate,
      GatewayProperties gatewayProperties,
      List<GlobalServletFilter> globalFilters) {
    return new ServletWebFilter(restTemplate, gatewayProperties, globalFilters);
  }

  @Bean
  public StripPrefixServletFilter stripPrefixServletFilter() {
    return new StripPrefixServletFilter();
  }

  @Bean
  public LogGlobalServletFilter globalServletFilter() {
    return new LogGlobalServletFilter();
  }

  @Bean
  @ConditionalOnMissingBean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public PathServletPredicate pathServletPredicate() {
    return new PathServletPredicate();
  }
}
