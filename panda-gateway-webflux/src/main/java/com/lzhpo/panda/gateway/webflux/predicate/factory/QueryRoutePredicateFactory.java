package com.lzhpo.panda.gateway.webflux.predicate.factory;

import com.lzhpo.panda.gateway.webflux.predicate.RoutePredicate;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by query.
 *
 * @author lzhpo
 */
@Slf4j
public class QueryRoutePredicateFactory
    extends AbstractRoutePredicateFactory<QueryRoutePredicateFactory.Config> {

  public QueryRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return serverWebExchange -> {
      String param = config.getParam();
      String regexp = config.getRegexp();

      ServerHttpRequest request = serverWebExchange.getRequest();
      return Optional.of(request.getQueryParams())
          .map(queryParamMap -> queryParamMap.getFirst(param))
          .filter(value -> !ObjectUtils.isEmpty(value))
          .map(value -> value.matches(regexp))
          .orElse(false);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotBlank private String param;

    @NotBlank private String regexp;
  }
}
