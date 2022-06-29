package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.Arrays;
import javax.servlet.http.Cookie;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
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
    return request -> {
      String cookie = config.getCookie();
      String regexp = config.getRegexp();
      Cookie[] cookies = request.getCookies();
      return !ObjectUtils.isEmpty(cookies)
          && Arrays.stream(cookies)
              .anyMatch(x -> x.getName().equalsIgnoreCase(cookie) && x.getValue().matches(regexp));
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotBlank private String cookie;

    @NotBlank private String regexp;
  }
}
