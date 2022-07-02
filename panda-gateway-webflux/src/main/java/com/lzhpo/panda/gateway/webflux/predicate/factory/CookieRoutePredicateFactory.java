package com.lzhpo.panda.gateway.webflux.predicate.factory;

import com.lzhpo.panda.gateway.webflux.predicate.RoutePredicate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
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
    return serverWebExchange -> {
      ServerHttpRequest request = serverWebExchange.getRequest();
      MultiValueMap<String, HttpCookie> requestCookies = request.getCookies();
      if (ObjectUtils.isEmpty(requestCookies)) {
        return false;
      }

      Map<String, String> configCookies = config.getCookies();
      for (Entry<String, String> requestCookieEntry : configCookies.entrySet()) {
        String requestCookieName = requestCookieEntry.getKey();
        String requestCookieValue = requestCookieEntry.getValue();
        String configCookieRegexp = configCookies.get(requestCookieName);
        if (Objects.nonNull(configCookieRegexp) && requestCookieValue.matches(configCookieRegexp)) {
          return true;
        }
      }

      return false;
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
