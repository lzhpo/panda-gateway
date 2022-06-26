package com.lzhpo.panda.gateway.predicate.factory;

import cn.hutool.core.collection.ListUtil;
import com.lzhpo.panda.gateway.core.config.ConfigTypeEnum;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
@Slf4j
public class BetweenRoutePredicateFactory
    extends AbstractRoutePredicateFactory<BetweenRoutePredicateFactory.Config> {

  public BetweenRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public List<String> configFieldOrder() {
    return ListUtil.of("start", "end");
  }

  @Override
  public ConfigTypeEnum configFieldType() {
    return ConfigTypeEnum.DEFAULT;
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
