/*
 * Copyright 2022 lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
