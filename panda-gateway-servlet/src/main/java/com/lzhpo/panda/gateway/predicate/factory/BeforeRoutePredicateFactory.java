package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by before time.
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
 *         - Before=2030-05-01T00:00:00+08:00[Asia/Shanghai]
 * }</pre>
 *
 * @author lzhpo
 */
@Slf4j
public class BeforeRoutePredicateFactory
    extends AbstractRoutePredicateFactory<BeforeRoutePredicateFactory.Config> {

  public BeforeRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      ZonedDateTime now = ZonedDateTime.now();
      ZonedDateTime zonedDateTime = config.getTime();
      return now.isBefore(zonedDateTime);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotNull private ZonedDateTime time;
  }
}
