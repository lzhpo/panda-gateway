package com.lzhpo.panda.gateway.webflux.predicate.factory;

import com.lzhpo.panda.gateway.webflux.predicate.RoutePredicate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by parameter.
 *
 * @author lzhpo
 */
@Slf4j
public class ParameterRoutePredicateFactory
    extends AbstractRoutePredicateFactory<ParameterRoutePredicateFactory.Config> {

  public ParameterRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return serverWebExchange -> {
      MultiValueMap<String, String> requestParameterMap =
          serverWebExchange.getRequest().getQueryParams();
      if (ObjectUtils.isEmpty(requestParameterMap)) {
        return false;
      }

      Map<String, String> configParameters = config.getParameters();
      for (Entry<String, String> configParameterEntry : configParameters.entrySet()) {
        String configParameterName = configParameterEntry.getKey();
        String configParameterRegexp = configParameterEntry.getValue();
        List<String> requestParameters = requestParameterMap.get(configParameterName);
        if (!ObjectUtils.isEmpty(requestParameters)
            && requestParameters.get(0).matches(configParameterRegexp)) {
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
     * Predicate with request parameters
     *
     * <pre>
     * key: parameter name
     * value: regexp expression
     * </pre>
     */
    @NotEmpty private Map<String, String> parameters;
  }
}
