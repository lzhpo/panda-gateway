package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.filter.RouteFilter;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class RemoveResponseHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<RemoveResponseHeaderRouteFilterFactory.Config>
    implements Ordered {

  public RemoveResponseHeaderRouteFilterFactory() {
    super(Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (exchange, filterChain) -> {
      Map<String, String> configHeaders = config.getHeaders();
      HttpHeaders respHeaders = exchange.getResponse().getHeaders();

      configHeaders.forEach(
          (configHeaderName, configHeaderRegexp) -> {
            String headerValue = respHeaders.getFirst(configHeaderName);
            if (Objects.nonNull(headerValue) && headerValue.matches(configHeaderRegexp)) {
              respHeaders.remove(configHeaderName);
            }
          });

      return filterChain.filter(exchange);
    };
  }

  @Data
  @Validated
  public static class Config {

    /**
     * Request headers to delete
     *
     * <pre>
     * key: response header name
     * value: regexp expression
     * </pre>
     */
    @NotEmpty private Map<String, String> headers;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
