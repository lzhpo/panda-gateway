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

package com.lzhpo.panda.gateway.handler;

import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.filter.DefaultRouteFilterChain;
import com.lzhpo.panda.gateway.filter.ForwardRouteFilter;
import com.lzhpo.panda.gateway.filter.GlobalFilterAdapter;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import com.lzhpo.panda.gateway.route.Route;
import com.lzhpo.panda.gateway.route.RouteComponentExtractor;
import com.lzhpo.panda.gateway.route.RouteLocator;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class GatewayRequestHandler implements WebHandler {

  private final RouteLocator routeLocator;
  private final WebClient.Builder webClientBuilder;

  @Override
  public Mono<Void> handle(ServerWebExchange exchange) {
    String routeId = exchange.getAttribute(GatewayConst.ROUTE_ID);
    List<GlobalFilterAdapter> globalFilters = RouteComponentExtractor.getGlobalFilterAdapters();
    List<RouteFilter> filters = new ArrayList<>(globalFilters);

    if (StringUtils.hasText(routeId)) {
      Route route = routeLocator.getRoute(routeId);
      Assert.notNull(route, "Cannot find route[" + routeId + "]");
      filters.addAll(route.getFilters());
      filters.add(new ForwardRouteFilter(route, webClientBuilder));
    }

    AnnotationAwareOrderComparator.sort(filters);
    return new DefaultRouteFilterChain(filters).filter(exchange);
  }
}
