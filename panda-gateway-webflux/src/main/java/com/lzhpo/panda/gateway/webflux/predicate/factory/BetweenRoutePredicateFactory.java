package com.lzhpo.panda.gateway.webflux.predicate.factory;

import com.lzhpo.panda.gateway.webflux.predicate.RoutePredicate;
import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by between time.
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
    return serverWebExchange -> {
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
