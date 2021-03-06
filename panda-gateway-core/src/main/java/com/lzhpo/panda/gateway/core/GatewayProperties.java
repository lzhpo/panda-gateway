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

package com.lzhpo.panda.gateway.core;

import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.route.RouteInitializer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

/**
 * @author lzhpo
 */
@Data
@AutoConfigureAfter({RouteInitializer.class})
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties implements InitializingBean {

  /** Whether to enable service discovery mode, otherwise, use http or https. */
  private boolean discovery = true;

  /** About http client configurations. */
  private HttpClientConfig httpClient;

  /** About redis configurations. */
  private RedisConfig redis;

  /** Gateway service routes */
  private List<RouteDefinition> routes = new ArrayList<>();

  /** Cross-domain configurations */
  private Map<String, CorsConfiguration> crossConfigurations = new HashMap<>();

  @Data
  public static class HttpClientConfig {

    /** Http client connect timeout */
    private Duration connectTimeout;

    /** Http client response timeout */
    private Duration responseTimeout;
  }

  @Data
  public static class RedisConfig {

    /** Whether enable redis */
    private boolean enabled = false;

    /** Use redis to save routes */
    private boolean routeLocator = false;
  }

  @Override
  public void afterPropertiesSet() {
    String[] names = SpringUtil.getBeanNamesForType(RouteInitializer.class);
    for (String name : names) {
      RouteInitializer routeInitializer = SpringUtil.getBean(name);
      routeInitializer.initialize(this.getRoutes());
    }
  }
}
