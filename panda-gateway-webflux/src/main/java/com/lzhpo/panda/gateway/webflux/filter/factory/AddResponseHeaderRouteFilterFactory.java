package com.lzhpo.panda.gateway.webflux.filter.factory;

import com.lzhpo.panda.gateway.webflux.filter.RouteFilter;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class AddResponseHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<AddResponseHeaderRouteFilterFactory.Config>
    implements Ordered {

  public AddResponseHeaderRouteFilterFactory() {
    super(Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (exchange, filterChain) -> {
      Map<String, String> headers = config.getHeaders();
      ServerHttpResponse response = exchange.getResponse();
      HttpHeaders respHeaders = response.getHeaders();
      headers.forEach(respHeaders::remove);
      return filterChain.filter(exchange);
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
