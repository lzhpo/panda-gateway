package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.filter.factory.AddHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.RemoveHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.StripPrefixRouteFilterFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzhpo
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
public class RouteFilterAutoConfiguration {

  @Bean
  public StripPrefixRouteFilterFactory stripPrefixFilterFactory() {
    return new StripPrefixRouteFilterFactory();
  }

  @Bean
  public AddHeaderRouteFilterFactory addHeaderRouteFilterFactory() {
    return new AddHeaderRouteFilterFactory();
  }

  @Bean
  public RemoveHeaderRouteFilterFactory removeHeaderRouteFilterFactory() {
    return new RemoveHeaderRouteFilterFactory();
  }
}
