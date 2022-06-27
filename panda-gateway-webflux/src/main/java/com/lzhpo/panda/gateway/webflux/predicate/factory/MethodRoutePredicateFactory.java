package com.lzhpo.panda.gateway.webflux.predicate.factory;

import com.lzhpo.panda.gateway.webflux.predicate.RoutePredicate;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by method.
 *
 * @author lzhpo
 */
@Slf4j
public class MethodRoutePredicateFactory
    extends AbstractRoutePredicateFactory<MethodRoutePredicateFactory.Config> {

  public MethodRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return serverWebExchange -> {
      List<String> methods = config.getMethods();
      ServerHttpRequest request = serverWebExchange.getRequest();
      return Optional.ofNullable(request.getMethod())
          .map(httpMethod -> methods.stream().anyMatch(httpMethod::matches))
          .orElse(false);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private List<String> methods;
  }
}
