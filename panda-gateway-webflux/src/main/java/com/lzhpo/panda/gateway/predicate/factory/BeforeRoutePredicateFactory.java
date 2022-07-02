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
    return serverWebExchange -> {
      ZonedDateTime nowTime = ZonedDateTime.now();
      ZonedDateTime beforeTime = config.getTime();
      return nowTime.isBefore(beforeTime);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotNull private ZonedDateTime time;
  }
}
