package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by method.
 *
 * <p>e.g:
 *
 * <pre>{@code
 * gateway:
 *   routes:
 *     - id: service1-sample
 *       uri: http://127.0.0.1:9000
 *       order: 1
 *       predicates:
 *         - Method=POST,GET
 * }</pre>
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
    return request -> {
      List<String> methods = config.getMethods();
      String method = request.getMethod();
      return methods.stream().anyMatch(x -> x.equalsIgnoreCase(method));
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private List<String> methods;
  }
}
