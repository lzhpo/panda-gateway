package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.Arrays;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by cookie.
 *
 * <p>e.g:
 *
 * <pre>{@code
 * gateway:
 *   routes:
 *     - id: service1-sample
 *       uri: http://127.0.0.1:9000
 *       order: 1
 *       predicates:
 *         - Cookie=Name,\d+
 * }</pre>
 *
 * <p>Notes: support regular expression for value.
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
      return Arrays.stream(request.getCookies())
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
