package com.lzhpo.panda.gateway.webflux.predicate.factory;

import com.lzhpo.panda.gateway.webflux.predicate.RoutePredicate;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by cookie.
 *
 * @author lzhpo
 */
@Slf4j
public class CookieRoutePredicateFactory
    extends AbstractRoutePredicateFactory<CookieRoutePredicateFactory.Config> {

  public CookieRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return serverWebExchange -> {
      String cookie = config.getCookie();
      String regexp = config.getRegexp();

      ServerHttpRequest request = serverWebExchange.getRequest();
      MultiValueMap<String, HttpCookie> cookies = request.getCookies();
      return Optional.ofNullable(cookies.getFirst(cookie))
          .map(HttpCookie::getValue)
          .map(value -> value.matches(regexp))
          .orElse(false);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotBlank private String cookie;

    @NotBlank private String regexp;
  }
}
