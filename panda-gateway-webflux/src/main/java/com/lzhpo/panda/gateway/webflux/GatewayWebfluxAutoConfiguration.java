package com.lzhpo.panda.gateway.webflux;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.webflux.predicate.PathServletPredicate;
import com.lzhpo.panda.gateway.webflux.predicate.WebfluxPredicate;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author lzhpo
 */
@Configuration
public class GatewayWebfluxAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public WebClient webClient() {
    return WebClient.create();
  }

  @Bean
  public PathServletPredicate pathServletPredicate() {
    return new PathServletPredicate();
  }

  @Bean
  public WebfluxForwardFilter webfluxForwardFilter(
      GatewayProperties gatewayProperties, WebClient webClient, List<WebfluxPredicate> predicates) {
    return new WebfluxForwardFilter(webClient, predicates, gatewayProperties);
  }
}
