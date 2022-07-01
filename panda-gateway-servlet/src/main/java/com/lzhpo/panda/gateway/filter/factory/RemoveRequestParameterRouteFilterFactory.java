package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.filter.RouteFilter;
import java.util.HashMap;
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
    return new HttpServletRequestWrapper(request) {

      @Override
      public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        Map<String, String[]> finalParameters = new HashMap<>(parameterMap);
        Map<String, String> parameters = config.getParameters();

        for (Entry<String, String> entry : parameters.entrySet()) {
          String name = entry.getKey();
          String regexp = entry.getValue();

          String[] parameterValues = parameterMap.get(name);
          if (!ObjectUtils.isEmpty(parameterValues) && parameterValues[0].matches(regexp)) {
            finalParameters.remove(name);
          }
        }

        return finalParameters;
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
