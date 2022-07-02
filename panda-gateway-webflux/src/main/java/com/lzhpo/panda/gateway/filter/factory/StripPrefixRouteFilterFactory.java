package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.core.utils.ExtractUtil;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import javax.validation.constraints.Min;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class StripPrefixRouteFilterFactory
    extends AbstractRouteFilterFactory<StripPrefixRouteFilterFactory.Config> implements Ordered {

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
                  builder -> builder.path(ExtractUtil.stripPrefix(requestPath, config.getParts())))
              .build());
    };
  }

  @Data
  @Validated
  public static class Config {

    @Min(1)
    private int parts;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
