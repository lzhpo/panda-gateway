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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Route filter just apply current route.
 *
 * @author lzhpo
 */
public interface RouteFilter {

  /**
   * Execute route filter.
   *
   * @param request {@link HttpServletRequest}
   * @param response {@link HttpServletResponse}
   * @param chain {@link RouteFilterChain}
   */
  void doFilter(HttpServletRequest request, HttpServletResponse response, RouteFilterChain chain);
}
