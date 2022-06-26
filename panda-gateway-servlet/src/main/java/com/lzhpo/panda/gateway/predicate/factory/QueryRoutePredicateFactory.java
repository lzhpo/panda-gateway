package com.lzhpo.panda.gateway.predicate.factory;

import cn.hutool.core.collection.ListUtil;
import com.lzhpo.panda.gateway.core.config.ConfigTypeEnum;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
@Slf4j
public class QueryRoutePredicateFactory
    extends AbstractRoutePredicateFactory<QueryRoutePredicateFactory.Config> {

  public QueryRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public List<String> configFieldOrder() {
    return ListUtil.of("param", "regexp");
  }

  @Override
  public ConfigTypeEnum configFieldType() {
    return ConfigTypeEnum.DEFAULT;
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      String param = config.getParam();
      String regexp = config.getRegexp();
      Map<String, String[]> parameterMap = request.getParameterMap();
      if (ObjectUtils.isEmpty(parameterMap)) {
        return false;
      }

      for (Entry<String, String[]> entry : parameterMap.entrySet()) {
        String paramKey = entry.getKey();
        if (paramKey.equals(param)) {
          String[] paramValues = entry.getValue();
          for (String paramValue : paramValues) {
            if (paramValue.matches(regexp)) {
              return true;
            }
          }
        }
      }

      return false;
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotBlank private String param;

    @NotBlank private String regexp;
  }
}
