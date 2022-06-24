package com.lzhpo.panda.gateway.servlet;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.servlet.filter.StripPrefixServletFilter;
import com.lzhpo.panda.gateway.servlet.predicate.PathServletPredicate;
import com.lzhpo.panda.gateway.servlet.predicate.ServletPredicate;
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
  public PathServletPredicate pathServletPredicate() {
    return new PathServletPredicate();
  }

  @Bean
  public StripPrefixServletFilter stripPrefixFilter() {
    return new StripPrefixServletFilter();
  }

  @Bean
  public ServletForwardFilter servletForwardFilter(
      GatewayProperties gatewayProperties,
      RestTemplate restTemplate,
      List<ServletPredicate> predicates) {
    return new ServletForwardFilter(restTemplate, predicates, gatewayProperties);
  }
}
