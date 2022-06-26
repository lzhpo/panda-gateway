package com.lzhpo.panda.gateway.predicate.factory;

import cn.hutool.core.collection.ListUtil;
import com.lzhpo.panda.gateway.core.config.ConfigTypeEnum;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.http.server.PathContainer;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * Route predicate by path.
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
 *         - Path=/api/service-sample/**,=/api/sample/**
 * }</pre>
 *
 * <p>Notes: support regular expression.
 *
 * @author lzhpo
 */
public class PathRoutePredicateFactory
    extends AbstractRoutePredicateFactory<PathRoutePredicateFactory.Config> {

  /** Also can see {@link AntPathMatcher} */
  private final PathPatternParser antPathMatcher = new PathPatternParser();

  public PathRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      String requestPath = request.getRequestURI();
      List<String> patterns = config.getPatterns();
      return patterns.stream()
          .anyMatch(
              pattern ->
                  antPathMatcher.parse(pattern).matches(PathContainer.parsePath(requestPath)));
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
  @Validated
  public static class Config {

    @NotEmpty private List<String> patterns;
  }
}
