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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by parameter.
 *
 * @author lzhpo
 */
@Slf4j
public class ParameterRoutePredicateFactory
    extends AbstractRoutePredicateFactory<ParameterRoutePredicateFactory.Config> {

  public ParameterRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return serverWebExchange -> {
      MultiValueMap<String, String> requestParameterMap =
          serverWebExchange.getRequest().getQueryParams();
      if (ObjectUtils.isEmpty(requestParameterMap)) {
        return false;
      }

      Map<String, String> configParameters = config.getParameters();
      for (Entry<String, String> configParameterEntry : configParameters.entrySet()) {
        String configParameterName = configParameterEntry.getKey();
        String configParameterRegexp = configParameterEntry.getValue();
        List<String> requestParameters = requestParameterMap.get(configParameterName);
        if (!ObjectUtils.isEmpty(requestParameters)
            && requestParameters.get(0).matches(configParameterRegexp)) {
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
     * Predicate with request parameters
     *
     * <pre>
     * key: parameter name
     * value: regexp expression
     * </pre>
     */
    @NotEmpty private Map<String, String> parameters;
  }
}
