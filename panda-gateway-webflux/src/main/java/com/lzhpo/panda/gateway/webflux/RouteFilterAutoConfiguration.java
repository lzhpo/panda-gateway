package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.webflux.filter.factory.AddHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.webflux.filter.factory.RemoveHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.webflux.filter.factory.StripPrefixRouteFilterFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzhpo
 */
@Configuration
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class RouteFilterAutoConfiguration {

  @Bean
  public StripPrefixRouteFilterFactory stripPrefixRouteFilterFactory() {
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
