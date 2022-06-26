package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by header.
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
 *         - Header=X-B3-TraceId,\d+
 * }</pre>
 *
 * <p>Notes: support regular expression for value.
 *
 * @author lzhpo
 */
@Slf4j
public class HeaderRoutePredicateFactory
    extends AbstractRoutePredicateFactory<HeaderRoutePredicateFactory.Config> {

  public HeaderRoutePredicateFactory() {
    super(Config.class);
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
