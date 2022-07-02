package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.filter.RouteFilter;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.validation.Valid;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class RemoveRequestParameterRouteFilterFactory
    extends AbstractRouteFilterFactory<RemoveRequestParameterRouteFilterFactory.Config>
    implements Ordered {

  public RemoveRequestParameterRouteFilterFactory() {
    super(RemoveRequestParameterRouteFilterFactory.Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (request, response, chain) ->
        chain.doFilter(modifyRequestIfNecessary(request, config), response);
  }

  private HttpServletRequestWrapper modifyRequestIfNecessary(
      HttpServletRequest request, Config config) {
    Map<String, String> configParameters = config.getParameters();
    return new HttpServletRequestWrapper(request) {

      @Override
      public Map<String, String[]> getParameterMap() {
        Map<String, String[]> originalParameterMap = super.getParameterMap();

        for (Entry<String, String> configParameterEntry : configParameters.entrySet()) {
          String configParameterName = configParameterEntry.getKey();
          String configParameterRegexp = configParameterEntry.getValue();
          String[] parameterValues = originalParameterMap.get(configParameterName);

          if (!ObjectUtils.isEmpty(parameterValues)
              && parameterValues[0].matches(configParameterRegexp)) {
            originalParameterMap.remove(configParameterName);
          }
        }

        return originalParameterMap;
      }
    };
  }

  @Data
  @Validated
  public static class Config {

    /**
     * Request parameters to delete
     *
     * <pre>
     * key: parameter name
     * value: regexp expression
     * </pre>
     */
    @Valid private Map<String, String> parameters;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}