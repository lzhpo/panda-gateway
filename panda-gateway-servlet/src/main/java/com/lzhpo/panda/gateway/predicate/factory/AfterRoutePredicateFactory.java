package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by after time.
 *
 * @author lzhpo
 */
@Slf4j
public class AfterRoutePredicateFactory
    extends AbstractRoutePredicateFactory<AfterRoutePredicateFactory.Config> {

  public AfterRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      ZonedDateTime now = ZonedDateTime.now();
      ZonedDateTime zonedDateTime = config.getTime();
      return now.isAfter(zonedDateTime);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotNull private ZonedDateTime time;
  }
}
