package com.lzhpo.panda.gateway.webflux.filter.factory;

import com.lzhpo.panda.gateway.webflux.filter.RouteFilter;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest.Builder;
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
    return (exchange, filterChain) ->
        filterChain.filter(
            exchange
                .mutate()
                .request(builder -> modifyRequestHeaderIfNecessary(builder, config))
                .build());
  }

  private void modifyRequestHeaderIfNecessary(Builder builder, Config config) {
    builder.headers(
        httpHeaders ->
            config
                .getHeaders()
                .forEach(
                    (configHeaderName, configHeaderRegexp) -> {
                      String headerValue = httpHeaders.getFirst(configHeaderName);
                      if (Objects.nonNull(headerValue) && headerValue.matches(configHeaderRegexp)) {
                        httpHeaders.remove(configHeaderName);
                      }
                    }));
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
