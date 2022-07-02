package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
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
      List<String> configMethods = config.getMethods();
      ServerHttpRequest request = serverWebExchange.getRequest();
      return Optional.ofNullable(request.getMethod())
          .map(requestMethod -> configMethods.stream().anyMatch(requestMethod::matches))
          .orElse(false);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private List<String> methods;
  }
}
