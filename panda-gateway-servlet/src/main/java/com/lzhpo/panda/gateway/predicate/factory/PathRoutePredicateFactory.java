package com.lzhpo.panda.gateway.predicate.factory;

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
 * @author lzhpo
 */
public class PathRoutePredicateFactory
    extends AbstractRoutePredicateFactory<PathRoutePredicateFactory.Config> {

  /** Also can see {@link AntPathMatcher} */
  private final PathPatternParser pathPatternParser = new PathPatternParser();

  public PathRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      String requestPath = request.getRequestURI();
      List<String> configPaths = config.getPaths();
      return configPaths.stream()
          .anyMatch(
              configPath ->
                  pathPatternParser
                      .parse(configPath)
                      .matches(PathContainer.parsePath(requestPath)));
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private List<String> paths;
  }
}
