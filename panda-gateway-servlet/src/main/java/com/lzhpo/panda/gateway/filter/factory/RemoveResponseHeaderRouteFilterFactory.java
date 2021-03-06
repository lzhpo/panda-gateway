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
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class RemoveResponseHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<RemoveResponseHeaderRouteFilterFactory.Config>
    implements Ordered {

  public RemoveResponseHeaderRouteFilterFactory() {
    super(RemoveResponseHeaderRouteFilterFactory.Config.class);
  }

  /**
   * Some notes reference:
   *
   * <pre>
   * - Tomcat: {@link org.apache.catalina.connector.Response#setHeader}
   * - Undertow: {@link io.undertow.servlet.spec.HttpServletResponseImpl#setHeader}
   * </pre>
   *
   * @param config config
   * @return {@link RouteFilter}
   */
  @Override
  public RouteFilter create(Config config) {
    return (request, response, chain) -> {
      Map<String, String> configHeaders = config.getHeaders();
      configHeaders.forEach(
          (configHeader, configRegexp) -> {
            String respHeaderValue = response.getHeader(configHeader);
            if (respHeaderValue.matches(configRegexp)) {
              // Tomcat doesn't support set response header value is null, but undertow can.
              response.setHeader(configHeader, null);
            }
          });
      chain.doFilter(request, response);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private Map<String, String> headers;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
