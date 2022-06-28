package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.filter.GlobalFilter;
import com.lzhpo.panda.gateway.support.ClientIpResolver;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author lzhpo
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
public class GatewayServletAutoConfiguration {

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
  @ConditionalOnMissingBean
  public RouteDefinitionLocator routeDefinitionLocator(
      GatewayProperties gatewayProperties, List<GlobalFilter> globalFilters) {
    return new MemoryRouteDefinitionLocator(gatewayProperties.getRoutes(), globalFilters);
  }

  @Bean
  public GatewayRequestMapping servletWebFilter(
      RestTemplate restTemplate, RouteDefinitionLocator routeDefinitionLocator) {
    return new GatewayRequestMapping(restTemplate, routeDefinitionLocator);
  }
}
