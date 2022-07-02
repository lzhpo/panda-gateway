package com.lzhpo.panda.gateway.webflux.predicate.factory;

import com.lzhpo.panda.gateway.core.route.BetweenZonedDateTimeConfig;
import com.lzhpo.panda.gateway.core.route.BetweenZonedDateTimeConfig.ZonedDateTimePair;
import com.lzhpo.panda.gateway.webflux.predicate.RoutePredicate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Route predicate by between time.
 *
 * @author lzhpo
 */
@Slf4j
public class BetweenRoutePredicateFactory
    extends AbstractRoutePredicateFactory<BetweenZonedDateTimeConfig> {

  public BetweenRoutePredicateFactory() {
    super(BetweenZonedDateTimeConfig.class);
  }

  @Override
  public RoutePredicate create(BetweenZonedDateTimeConfig config) {
    return serverWebExchange -> {
      ZonedDateTime nowTime = ZonedDateTime.now();
      List<ZonedDateTimePair> times = config.getTimes();
      return times.stream()
          .anyMatch(
              zonedDateTimePair -> {
                ZonedDateTime startTime = zonedDateTimePair.getStart();
                ZonedDateTime endTime = zonedDateTimePair.getEnd();
                return nowTime.isAfter(startTime) && nowTime.isBefore(endTime);
              });
    };
  }
}
