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
 * Route predicate by after time.
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
 *         - After=2019-04-29T00:00:00+08:00[Asia/Shanghai]
 * }</pre>
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
  public List<String> configFieldOrder() {
    return ListUtil.of("zonedDateTime");
  }

  @Override
  public ConfigTypeEnum configFieldType() {
    return ConfigTypeEnum.DEFAULT;
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      ZonedDateTime now = ZonedDateTime.now();
      ZonedDateTime zonedDateTime = config.getZonedDateTime();
      return now.isAfter(zonedDateTime);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotNull private ZonedDateTime zonedDateTime;
  }
}
