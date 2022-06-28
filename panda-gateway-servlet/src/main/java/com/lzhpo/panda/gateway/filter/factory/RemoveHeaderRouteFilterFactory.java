package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.filter.RouteFilter;
import com.lzhpo.panda.gateway.support.ModifyHeaderRequestWrapper;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class RemoveHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<RemoveHeaderRouteFilterFactory.Config> implements Ordered {

  public RemoveHeaderRouteFilterFactory() {
    super(RemoveHeaderRouteFilterFactory.Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (request, response, chain) -> {
      List<String> removeHeaders = config.getHeaders();
      chain.doFilter(ModifyHeaderRequestWrapper.removeHeaders(request, removeHeaders), response);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private List<String> headers;
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
