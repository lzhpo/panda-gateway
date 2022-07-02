package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by header.
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
      Map<String, String> configHeaders = config.getHeaders();
      for (Entry<String, String> configHeaderEntry : configHeaders.entrySet()) {
        String configHeaderName = configHeaderEntry.getKey();
        String configHeaderRegexp = configHeaderEntry.getValue();
        String requestHeaderValue = request.getHeader(configHeaderName);
        if (Objects.nonNull(requestHeaderValue) && requestHeaderValue.matches(configHeaderRegexp)) {
          return true;
        }
      }
      return false;
    };
  }

  @Data
  @Validated
  public static class Config {

    /**
     * Predicate with headers
     *
     * <pre>
     * key: header name
     * value: regexp expression
     * </pre>
     */
    @NotEmpty private Map<String, String> headers;
  }
}
