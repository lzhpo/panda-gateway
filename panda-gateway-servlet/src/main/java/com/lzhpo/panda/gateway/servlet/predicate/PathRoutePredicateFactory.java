package com.lzhpo.panda.gateway.servlet.predicate;

import cn.hutool.core.collection.ListUtil;
import com.lzhpo.panda.gateway.servlet.enums.ConfigTypeEnum;
import com.lzhpo.panda.gateway.servlet.predicate.PathRoutePredicateFactory.Config;
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
  public ServletPredicate apply(Config config) {
    return (request) -> {
      String requestPath = request.getRequestURI();
      List<String> patterns = config.getPatterns();
      return patterns.stream().anyMatch(pattern -> antPathMatcher.match(pattern, requestPath));
    };
  }

  @Override
  public ConfigTypeEnum configTypeEnum() {
    return ConfigTypeEnum.LIST;
  }

  @Override
  public List<String> configFieldNames() {
    return ListUtil.of("patterns");
  }

  @Data
  public static class Config {
    private List<String> patterns = new ArrayList<>();
  }
}
