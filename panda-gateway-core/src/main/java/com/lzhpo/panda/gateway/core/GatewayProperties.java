package com.lzhpo.panda.gateway.core;

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

  private List<Route> routes = new ArrayList<>();
}
