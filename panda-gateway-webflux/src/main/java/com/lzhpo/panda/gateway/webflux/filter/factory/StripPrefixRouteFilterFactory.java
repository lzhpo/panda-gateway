package com.lzhpo.panda.gateway.webflux.filter.factory;

import com.lzhpo.panda.gateway.core.utils.ExtractUtil;
import com.lzhpo.panda.gateway.webflux.filter.RouteFilter;
import com.lzhpo.panda.gateway.webflux.filter.factory.StripPrefixRouteFilterFactory.Config;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @author lzhpo
 */
public class StripPrefixRouteFilterFactory extends AbstractRouteFilterFactory<Config>
    implements Ordered {

  public StripPrefixRouteFilterFactory() {
    super(Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (exchange, filterChain) -> {
      ServerHttpRequest request = exchange.getRequest();
      String requestPath = request.getPath().value();
      return filterChain.filter(
          exchange
              .mutate()
              .request(
                  builder -> {
                    builder.path(ExtractUtil.stripPrefix(requestPath, config.getParts()));
                  })
              .build());
    };
  }

  @Data
  public static class Config {

    private int parts;
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
