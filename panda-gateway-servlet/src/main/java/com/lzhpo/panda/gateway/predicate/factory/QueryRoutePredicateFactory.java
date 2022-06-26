package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.Map;
import java.util.Map.Entry;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by query.
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
 *         - Query=name,Lewis
 * }</pre>
 *
 * <p>Notes: support regular expression for value.
 *
 * @author lzhpo
 */
@Slf4j
public class QueryRoutePredicateFactory
    extends AbstractRoutePredicateFactory<QueryRoutePredicateFactory.Config> {

  public QueryRoutePredicateFactory() {
    super(Config.class);
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
