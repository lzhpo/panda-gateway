package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by between time.
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
 *         - Between=2019-04-29T00:00:00+08:00[Asia/Shanghai], 2030-05-01T00:00:00+08:00[Asia/Shanghai]
 * }</pre>
 *
 * @author lzhpo
 */
@Slf4j
public class BetweenRoutePredicateFactory
    extends AbstractRoutePredicateFactory<BetweenRoutePredicateFactory.Config> {

  public BetweenRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      ZonedDateTime now = ZonedDateTime.now();
      ZonedDateTime start = config.getStart();
      ZonedDateTime end = config.getEnd();
      return now.isAfter(start) && now.isBefore(end);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotNull private ZonedDateTime start;

    @NotNull private ZonedDateTime end;
  }
}
