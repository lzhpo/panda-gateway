package com.lzhpo.panda.gateway.webflux.predicate.factory;

import com.lzhpo.panda.gateway.webflux.predicate.RoutePredicate;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by header.
 *
 * @author lzhpo
 */
@Slf4j
public class HeaderRoutePredicateFactory
    extends AbstractRoutePredicateFactory<HeaderRoutePredicateFactory.Config> {

  public HeaderRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return serverWebExchange -> {
      String header = config.getHeader();
      String regexp = config.getRegexp();

      ServerHttpRequest request = serverWebExchange.getRequest();
      HttpHeaders headers = request.getHeaders();
      return Optional.ofNullable(headers.getFirst(header))
          .map(value -> value.matches(regexp))
          .orElse(false);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotBlank private String header;

    @NotBlank private String regexp;
  }
}
