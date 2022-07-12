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
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * Route predicate by path.
 *
 * @author lzhpo
 */
public class PathRoutePredicateFactory
    extends AbstractRoutePredicateFactory<PathRoutePredicateFactory.Config> {

  /** Also can see {@link AntPathMatcher} */
  private final PathPatternParser pathPatternParser = new PathPatternParser();

  public PathRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return serverWebExchange -> {
      ServerHttpRequest request = serverWebExchange.getRequest();
      String requestPath = request.getPath().value();
      List<String> configPaths = config.getPaths();
      return configPaths.stream()
          .anyMatch(
              configPath ->
                  pathPatternParser
                      .parse(configPath)
                      .matches(PathContainer.parsePath(requestPath)));
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private List<String> paths;
  }
}
