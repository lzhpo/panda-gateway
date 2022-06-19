package com.lzhpo.gateway.core;

import lombok.Data;

/**
 * @author lzhpo
 */
@Data
public class GatewayProxyRoute {

  private String name;
  private String matchPath;
  private String targetUrl;
  private Integer stripPrefix;
}
