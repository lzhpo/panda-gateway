package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.filter.RouteFilter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class AddRequestParameterRouteFilterFactory
    extends AbstractRouteFilterFactory<AddRequestParameterRouteFilterFactory.Config>
    implements Ordered {

  public AddRequestParameterRouteFilterFactory() {
    super(AddRequestParameterRouteFilterFactory.Config.class);
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
        parameters.forEach((name, value) -> finalParameters.put(name, new String[] {value}));
        return finalParameters;
      }
    };
  }

  @Data
  @Validated
  public static class Config {

    /**
     * Request parameters to add
     *
     * <pre>
     * key: parameter name
     * value: parameter value
     * </pre>
     */
    @NotEmpty private Map<String, String> parameters;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
