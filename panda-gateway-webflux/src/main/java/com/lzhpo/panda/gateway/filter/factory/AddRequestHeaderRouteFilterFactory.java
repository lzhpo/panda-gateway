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
public class AddRequestHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<AddRequestHeaderRouteFilterFactory.Config>
    implements Ordered {

  public AddRequestHeaderRouteFilterFactory() {
    super(Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (exchange, filterChain) -> {
      Map<String, String> headers = config.getHeaders();
      return filterChain.filter(
          exchange
              .mutate()
              .request(builder -> builder.headers(httpHeaders -> headers.forEach(httpHeaders::add)))
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
    return Ordered.LOWEST_PRECEDENCE;
  }
}
