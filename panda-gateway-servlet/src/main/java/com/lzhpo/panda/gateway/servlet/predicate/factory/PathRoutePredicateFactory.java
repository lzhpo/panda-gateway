package com.lzhpo.panda.gateway.servlet.predicate.factory;

import cn.hutool.core.collection.ListUtil;
import com.lzhpo.panda.gateway.core.config.ConfigTypeEnum;
import com.lzhpo.panda.gateway.servlet.predicate.RoutePredicate;
import com.lzhpo.panda.gateway.servlet.predicate.factory.PathRoutePredicateFactory.Config;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.util.AntPathMatcher;

/**
 * @author lzhpo
 */
public class PathRoutePredicateFactory extends AbstractRoutePredicateFactory<Config> {

  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  public PathRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      String requestPath = request.getRequestURI();
      List<String> patterns = config.getPatterns();
      return patterns.stream().anyMatch(pattern -> antPathMatcher.match(pattern, requestPath));
    };
  }

  @Override
  public ConfigTypeEnum configFieldType() {
    return ConfigTypeEnum.LIST;
  }

  @Override
  public List<String> configFieldOrder() {
    return ListUtil.of("patterns");
  }

  @Data
  public static class Config {
    private List<String> patterns = new ArrayList<>();
  }
}
