package com.lzhpo.panda.gateway.predicate.factory;

import cn.hutool.core.collection.ListUtil;
import com.lzhpo.panda.gateway.core.config.ConfigTypeEnum;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
@Slf4j
public class HeaderRoutePredicateFactory
    extends AbstractRoutePredicateFactory<HeaderRoutePredicateFactory.Config> {

  public HeaderRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public List<String> configFieldOrder() {
    return ListUtil.of("header", "regexp");
  }

  @Override
  public ConfigTypeEnum configFieldType() {
    return ConfigTypeEnum.DEFAULT;
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      String header = config.getHeader();
      String regexp = config.getRegexp();

      String value = request.getHeader(header);
      if (Objects.isNull(value)) {
        return false;
      }
      return value.matches(regexp);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotBlank private String header;

    @NotBlank private String regexp;
  }
}
