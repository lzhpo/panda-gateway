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

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RelationType;
import com.lzhpo.panda.gateway.core.route.RouteMetadataConst;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import com.lzhpo.panda.gateway.route.Route;
import com.lzhpo.panda.gateway.route.RouteLocator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

/**
 * @author lzhpo
 */
@Slf4j
public class GatewayRequestMapping extends AbstractHandlerMapping {

  private final RouteLocator routeLocator;
  private final RestTemplate restTemplate;
  private final ServletContext servletContext;
  private final GatewayProperties gatewayProperties;

  /**
   * {@link org.springframework.boot.autoconfigure.web.servlet.WelcomePageHandlerMapping} <br>
   * {@link org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry#order} <br>
   * {@link org.springframework.web.servlet.DispatcherServlet#getHandler} <br>
   * {@link org.springframework.web.servlet.handler.SimpleUrlHandlerMapping}
   */
  public GatewayRequestMapping(
      ServletContext servletContext,
      RouteLocator routeLocator,
      RestTemplate restTemplate,
      GatewayProperties gatewayProperties) {
    this.servletContext = servletContext;
    this.routeLocator = routeLocator;
    this.restTemplate = restTemplate;
    this.gatewayProperties = gatewayProperties;
    setOrder(Ordered.LOWEST_PRECEDENCE - 2);
  }

  @Override
  protected Object getHandlerInternal(@NonNull HttpServletRequest request) {
    return lookupRoute(request)
        .map(
            route ->
                new GatewayRequestHandler(servletContext, route, restTemplate, gatewayProperties))
        .orElse(null);
  }

  /**
   * Execute route predicate, in order to find match route.
   *
   * @param request {@link HttpServletRequest}
   * @return matched route
   */
  private Optional<Route> lookupRoute(HttpServletRequest request) {
    List<Route> routes = routeLocator.getRoutes();
    return routes.stream()
        .filter(
            route -> {
              request.setAttribute(GatewayConst.ROUTE_ID, route.getId());
              List<RoutePredicate> predicates = route.getPredicates();
              Map<String, String> metadata = route.getMetadata();
              String relation = metadata.get(RouteMetadataConst.PREDICATE_RELATION);
              if (RelationType.OR.name().equalsIgnoreCase(relation)) {
                return predicates.stream().anyMatch(predicate -> predicate.test(request));
              }
              return predicates.stream().allMatch(predicate -> predicate.test(request));
            })
        .findFirst();
  }
}
