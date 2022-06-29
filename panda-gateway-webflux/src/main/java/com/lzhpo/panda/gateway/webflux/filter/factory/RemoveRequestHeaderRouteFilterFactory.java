package com.lzhpo.panda.gateway.webflux.filter.factory;

import com.lzhpo.panda.gateway.webflux.filter.RouteFilter;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class RemoveRequestHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<RemoveRequestHeaderRouteFilterFactory.Config>
    implements Ordered {

  public RemoveRequestHeaderRouteFilterFactory() {
    super(Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (exchange, filterChain) -> {
      List<String> headers = config.getHeaders();
      return filterChain.filter(
          exchange
              .mutate()
              .request(
                  builder -> builder.headers(httpHeaders -> headers.forEach(httpHeaders::remove)))
              .build());
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private List<String> headers;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
