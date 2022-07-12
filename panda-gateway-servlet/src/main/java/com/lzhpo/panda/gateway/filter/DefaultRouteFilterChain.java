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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;

/**
 * Default filter chain for route.
 *
 * @author lzhpo
 */
@Getter
public class DefaultRouteFilterChain implements RouteFilterChain {

  private final int index;
  private final List<RouteFilter> filters;

  public static DefaultRouteFilterChain create(List<RouteFilter> filters) {
    return new DefaultRouteFilterChain(filters);
  }

  private DefaultRouteFilterChain(List<RouteFilter> filters) {
    this.index = 0;
    this.filters = filters;
  }

  private DefaultRouteFilterChain(DefaultRouteFilterChain parent, int index) {
    this.filters = parent.getFilters();
    this.index = index;
  }

  @Override
  public void doFilter(HttpServletRequest request, HttpServletResponse response) {
    if (index < filters.size()) {
      RouteFilter filter = filters.get(index);
      DefaultRouteFilterChain chain = new DefaultRouteFilterChain(this, index + 1);
      filter.doFilter(request, response, chain);
    }
  }
}
