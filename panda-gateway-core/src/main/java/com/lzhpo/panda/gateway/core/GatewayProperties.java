package com.lzhpo.panda.gateway.core;

import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lzhpo
 */
@Data
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

  /** Whether to enable service discovery mode, otherwise, use http or https. */
  private boolean discovery = true;

  /** About redis configurations. */
  private RedisConfig redis;

  /** Gateway service routes */
  private List<RouteDefinition> routes = new ArrayList<>();

  @Data
  public static class RedisConfig {

    private boolean enabled = false;

    /** Use redis to save routes */
    private boolean routeLocator = false;
  }
}
