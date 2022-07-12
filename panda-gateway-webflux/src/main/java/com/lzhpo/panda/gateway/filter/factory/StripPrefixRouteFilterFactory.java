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

import com.lzhpo.panda.gateway.core.utils.ExtractUtil;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import javax.validation.constraints.Min;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class StripPrefixRouteFilterFactory
    extends AbstractRouteFilterFactory<StripPrefixRouteFilterFactory.Config> implements Ordered {

  public StripPrefixRouteFilterFactory() {
    super(Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (exchange, filterChain) -> {
      ServerHttpRequest request = exchange.getRequest();
      String requestPath = request.getPath().value();
      return filterChain.filter(
          exchange
              .mutate()
              .request(
                  builder -> builder.path(ExtractUtil.stripPrefix(requestPath, config.getParts())))
              .build());
    };
  }

  @Data
  @Validated
  public static class Config {

    @Min(1)
    private int parts;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
