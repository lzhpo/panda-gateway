package com.lzhpo.panda.gateway.core;

import lombok.Data;

/**
 * Route definition
 *
 * @author lzhpo
 */
@Data
public class RouteDefinition {

  private String name;
  private String matchPath;
  private String targetUrl;
  private Integer stripPrefix;
}
