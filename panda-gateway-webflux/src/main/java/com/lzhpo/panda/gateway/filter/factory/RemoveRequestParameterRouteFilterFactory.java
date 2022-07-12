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

package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.filter.RouteFilter;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class RemoveRequestParameterRouteFilterFactory
    extends AbstractRouteFilterFactory<RemoveRequestParameterRouteFilterFactory.Config>
    implements Ordered {

  public RemoveRequestParameterRouteFilterFactory() {
    super(Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (exchange, filterChain) ->
        filterChain.filter(
            exchange
                .mutate()
                .request(modifyRequestIfNecessary(exchange.getRequest(), config))
                .build());
  }

  private ServerHttpRequestDecorator modifyRequestIfNecessary(
      ServerHttpRequest request, Config config) {
    Map<String, String> configParameters = config.getParameters();
    return new ServerHttpRequestDecorator(request) {

      @Override
      public MultiValueMap<String, String> getQueryParams() {
        MultiValueMap<String, String> originalQueryParams = super.getQueryParams();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(originalQueryParams);

        configParameters.forEach(
            (configParameterName, configParameterRegexp) -> {
              String queryParamValue = originalQueryParams.getFirst(configParameterName);
              if (Objects.nonNull(queryParamValue)
                  && queryParamValue.matches(configParameterRegexp)) {
                queryParams.remove(configParameterName);
              }
            });

        return CollectionUtils.unmodifiableMultiValueMap(queryParams);
      }
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private Map<String, String> parameters;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
