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

package com.lzhpo.panda.gateway.filter;

import java.util.List;
import lombok.Getter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Getter
public class DefaultRouteFilterChain implements RouteFilterChain {

  private final int index;
  private final List<RouteFilter> filters;

  public DefaultRouteFilterChain(List<RouteFilter> filters) {
    this.filters = filters;
    this.index = 0;
  }

  private DefaultRouteFilterChain(DefaultRouteFilterChain parent, int index) {
    this.filters = parent.getFilters();
    this.index = index;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange) {
    return Mono.defer(
        () -> {
          if (index < filters.size()) {
            RouteFilter filter = filters.get(index);
            DefaultRouteFilterChain chain = new DefaultRouteFilterChain(this, index + 1);
            return filter.filter(exchange, chain);
          } else {
            return Mono.empty();
          }
        });
  }
}
