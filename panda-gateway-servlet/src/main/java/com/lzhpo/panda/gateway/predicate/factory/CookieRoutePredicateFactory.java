package com.lzhpo.panda.gateway.predicate.factory;

import cn.hutool.core.collection.ListUtil;
import com.lzhpo.panda.gateway.core.config.ConfigTypeEnum;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
@Slf4j
public class CookieRoutePredicateFactory
    extends AbstractRoutePredicateFactory<CookieRoutePredicateFactory.Config> {

  public CookieRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public List<String> configFieldOrder() {
    return ListUtil.of("cookie", "regexp");
  }

  @Override
  public ConfigTypeEnum configFieldType() {
    return ConfigTypeEnum.DEFAULT;
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      String cookie = config.getCookie();
      String regexp = config.getRegexp();
      return Arrays.stream(request.getCookies())
          .anyMatch(x -> x.getName().equalsIgnoreCase(cookie) && x.getValue().matches(regexp));
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotBlank private String cookie;

    @NotBlank private String regexp;
  }
}
