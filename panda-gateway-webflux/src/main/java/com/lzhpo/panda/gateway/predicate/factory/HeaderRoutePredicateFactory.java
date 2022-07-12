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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
      Map<String, String> configHeaders = config.getHeaders();
      HttpHeaders requestHeaders = serverWebExchange.getRequest().getHeaders();
      for (Entry<String, String> configHeaderEntry : configHeaders.entrySet()) {
        String configHeaderName = configHeaderEntry.getKey();
        String configHeaderRegexp = configHeaderEntry.getValue();
        String requestHeaderValue = requestHeaders.getFirst(configHeaderName);
        if (Objects.nonNull(requestHeaderValue) && requestHeaderValue.matches(configHeaderRegexp)) {
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
     * Predicate with headers
     *
     * <pre>
     * key: header name
     * value: regexp expression
     * </pre>
     */
    @NotEmpty private Map<String, String> headers;
  }
}
