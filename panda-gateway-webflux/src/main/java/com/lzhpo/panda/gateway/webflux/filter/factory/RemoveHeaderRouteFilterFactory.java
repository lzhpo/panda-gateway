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
public class RemoveHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<RemoveHeaderRouteFilterFactory.Config> implements Ordered {

  public RemoveHeaderRouteFilterFactory() {
    super(Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (exchange, filterChain) -> {
      List<String> removeHeaders = config.getHeaders();
      return filterChain.filter(
          exchange
              .mutate()
              .request(
                  builder ->
                      builder.headers(httpHeaders -> removeHeaders.forEach(httpHeaders::remove)))
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
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
