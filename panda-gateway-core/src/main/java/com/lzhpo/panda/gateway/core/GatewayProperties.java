package com.lzhpo.panda.gateway.core;

import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.core.route.RouteInitializer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
