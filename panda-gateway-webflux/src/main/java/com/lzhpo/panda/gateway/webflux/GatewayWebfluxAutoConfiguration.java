package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.webflux.filter.GlobalWebfluxFilter;
import com.lzhpo.panda.gateway.webflux.filter.LogGlobalWebfluxFilter;
import com.lzhpo.panda.gateway.webflux.filter.StripPrefixWebfluxFilter;
import com.lzhpo.panda.gateway.webflux.handler.WebfluxHandlerMapping;
import com.lzhpo.panda.gateway.webflux.handler.WebfluxWebHandler;
import com.lzhpo.panda.gateway.webflux.predicate.PathWebfluxPredicate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author lzhpo
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(DispatcherHandler.class)
public class GatewayWebfluxAutoConfiguration {

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public LogGlobalWebfluxFilter logGlobalWebfluxFilter() {
    return new LogGlobalWebfluxFilter();
  }

  @Bean
  public WebfluxWebHandler webfluxWebHandler(
      WebClient webClient, List<GlobalWebfluxFilter> globalFilters) {
    return new WebfluxWebHandler(webClient, globalFilters);
  }

  @Bean
  public StripPrefixWebfluxFilter stripPrefixWebfluxFilter() {
    return new StripPrefixWebfluxFilter();
  }

  @Bean
  public WebfluxHandlerMapping webfluxHandlerMapping(
      GatewayProperties gatewayProperties, WebfluxWebHandler webfluxWebHandler) {
    return new WebfluxHandlerMapping(webfluxWebHandler, gatewayProperties);
  }

  @Bean
  @ConditionalOnMissingBean
  public WebClient webClient() {
    return WebClient.create();
  }

  @Bean
  public PathWebfluxPredicate pathWebfluxPredicate() {
    return new PathWebfluxPredicate();
  }
}
