package com.lzhpo.panda.gateway.webflux.filter.factory;

import com.lzhpo.panda.gateway.webflux.filter.RouteFilter;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class AddHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<AddHeaderRouteFilterFactory.Config> implements Ordered {

  public AddHeaderRouteFilterFactory() {
    super(Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (exchange, filterChain) -> {
      Map<String, String> addHeaders = config.getHeaders();
      return filterChain.filter(
          exchange
              .mutate()
              .request(
                  builder -> builder.headers(httpHeaders -> addHeaders.forEach(httpHeaders::add)))
              .build());
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private Map<String, String> headers;
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
