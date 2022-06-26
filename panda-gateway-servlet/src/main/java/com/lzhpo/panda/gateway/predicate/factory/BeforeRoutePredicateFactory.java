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
