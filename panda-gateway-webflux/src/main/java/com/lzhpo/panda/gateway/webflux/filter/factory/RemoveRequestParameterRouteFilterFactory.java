package com.lzhpo.panda.gateway.webflux.filter.factory;

import com.lzhpo.panda.gateway.webflux.filter.RouteFilter;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class RemoveRequestParameterRouteFilterFactory
    extends AbstractRouteFilterFactory<RemoveRequestParameterRouteFilterFactory.Config>
    implements Ordered {

  public RemoveRequestParameterRouteFilterFactory() {
    super(Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (exchange, filterChain) ->
        filterChain.filter(
            exchange
                .mutate()
                .request(modifyRequestIfNecessary(exchange.getRequest(), config))
                .build());
  }

  private ServerHttpRequestDecorator modifyRequestIfNecessary(
      ServerHttpRequest request, Config config) {
    Map<String, String> parameters = config.getParameters();
    return new ServerHttpRequestDecorator(request) {

      @Override
      public MultiValueMap<String, String> getQueryParams() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.putAll(super.getQueryParams());
        parameters.forEach((name, value) -> queryParams.remove(name));
        return CollectionUtils.unmodifiableMultiValueMap(queryParams);
      }
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private Map<String, String> parameters;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
