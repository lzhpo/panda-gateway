package com.lzhpo.panda.gateway.servlet.predicate;

import cn.hutool.core.collection.ListUtil;
import com.lzhpo.panda.gateway.core.config.ConfigTypeEnum;
import com.lzhpo.panda.gateway.servlet.predicate.PathPredicateFactory.Config;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.util.AntPathMatcher;

/**
 * @author lzhpo
 */
public class PathPredicateFactory extends AbstractPredicateFactory<Config>
    implements PredicateInvoker<Config> {

  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  public PathPredicateFactory() {
    super(Config.class);
  }

  @Override
  public Predicate<HttpServletRequest> invoke(Config config) {
    return (request) -> {
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
