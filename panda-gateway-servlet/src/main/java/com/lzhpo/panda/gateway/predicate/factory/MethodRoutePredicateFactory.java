package com.lzhpo.panda.gateway.predicate.factory;

import cn.hutool.core.collection.ListUtil;
import com.lzhpo.panda.gateway.core.config.ConfigTypeEnum;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
@Slf4j
public class MethodRoutePredicateFactory
    extends AbstractRoutePredicateFactory<MethodRoutePredicateFactory.Config> {

  public MethodRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public List<String> configFieldOrder() {
    return ListUtil.of("methods");
  }

  @Override
  public ConfigTypeEnum configFieldType() {
    return ConfigTypeEnum.LIST;
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      List<String> methods = config.getMethods();
      String method = request.getMethod();
      return methods.stream().anyMatch(x -> x.equalsIgnoreCase(method));
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private List<String> methods;
  }
}
