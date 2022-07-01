package com.lzhpo.panda.gateway.webflux.filter.factory;

import com.lzhpo.panda.gateway.webflux.filter.RouteFilter;
import java.util.Map;
import java.util.Objects;
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
      Map<String, String> headers = config.getHeaders();
      return filterChain.filter(
          exchange
              .mutate()
              .request(
                  builder ->
                      builder.headers(
                          httpHeaders ->
                              headers.forEach(
                                  (name, regexp) -> {
                                    String headerValue = httpHeaders.getFirst(name);
                                    if (Objects.nonNull(headerValue)
                                        && headerValue.matches(regexp)) {
                                      httpHeaders.remove(name);
                                    }
                                  })))
              .build());
    };
  }

  @Data
  @Validated
  public static class Config {

    /**
     * Request headers to delete
     *
     * <pre>
     * key: header name
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
