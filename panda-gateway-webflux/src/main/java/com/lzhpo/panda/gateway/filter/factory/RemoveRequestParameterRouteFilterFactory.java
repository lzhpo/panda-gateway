package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.filter.RouteFilter;
import java.util.Map;
import java.util.Objects;
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
    Map<String, String> configParameters = config.getParameters();
    return new ServerHttpRequestDecorator(request) {

      @Override
      public MultiValueMap<String, String> getQueryParams() {
        MultiValueMap<String, String> originalQueryParams = super.getQueryParams();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(originalQueryParams);

        configParameters.forEach(
            (configParameterName, configParameterRegexp) -> {
              String queryParamValue = originalQueryParams.getFirst(configParameterName);
              if (Objects.nonNull(queryParamValue)
                  && queryParamValue.matches(configParameterRegexp)) {
                queryParams.remove(configParameterName);
              }
            });

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
