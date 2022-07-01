package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.webflux.filter.factory.AddRequestHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.webflux.filter.factory.AddRequestParameterRouteFilterFactory;
import com.lzhpo.panda.gateway.webflux.filter.factory.AddResponseHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.webflux.filter.factory.RemoveRequestHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.webflux.filter.factory.RemoveRequestParameterRouteFilterFactory;
import com.lzhpo.panda.gateway.webflux.filter.factory.RemoveResponseHeaderRouteFilterFactory;
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
  public AddRequestParameterRouteFilterFactory addRequestParameterRouteFilterFactory() {
    return new AddRequestParameterRouteFilterFactory();
  }

  @Bean
  public RemoveRequestParameterRouteFilterFactory removeRequestParameterRouteFilterFactory() {
    return new RemoveRequestParameterRouteFilterFactory();
  }

  @Bean
  public StripPrefixRouteFilterFactory stripPrefixRouteFilterFactory() {
    return new StripPrefixRouteFilterFactory();
  }

  @Bean
  public AddRequestHeaderRouteFilterFactory addRequestHeaderRouteFilterFactory() {
    return new AddRequestHeaderRouteFilterFactory();
  }

  @Bean
  public RemoveRequestHeaderRouteFilterFactory removeRequestHeaderRouteFilterFactory() {
    return new RemoveRequestHeaderRouteFilterFactory();
  }

  @Bean
  public AddResponseHeaderRouteFilterFactory addResponseHeaderRouteFilterFactory() {
    return new AddResponseHeaderRouteFilterFactory();
  }

  @Bean
  public RemoveResponseHeaderRouteFilterFactory removeResponseHeaderRouteFilterFactory() {
    return new RemoveResponseHeaderRouteFilterFactory();
  }
}
