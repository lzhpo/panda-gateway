package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.validation.constraints.NotEmpty;
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
      Cookie[] requestCookies = request.getCookies();
      if (ObjectUtils.isEmpty(requestCookies)) {
        return false;
      }

      Map<String, String> configCookies = config.getCookies();
      return Arrays.stream(requestCookies)
          .anyMatch(
              requestCookie -> {
                String requestCookieName = requestCookie.getName();
                String requestCookieValue = requestCookie.getValue();
                String configCookieRegexp = configCookies.get(requestCookieName);
                return configCookies.containsKey(requestCookieName)
                    && requestCookieValue.matches(configCookieRegexp);
              });
    };
  }

  @Data
  @Validated
  public static class Config {

    /**
     * Predicate with cookies
     *
     * <pre>
     * key: cookie name
     * value: regexp expression
     * </pre>
     */
    @NotEmpty private Map<String, String> cookies;
  }
}
