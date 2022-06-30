package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.filter.RouteFilter;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class RemoveResponseHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<RemoveResponseHeaderRouteFilterFactory.Config>
    implements Ordered {

  public RemoveResponseHeaderRouteFilterFactory() {
    super(RemoveResponseHeaderRouteFilterFactory.Config.class);
  }

  /**
   * {@link org.apache.catalina.connector.Response#setHeader(java.lang.String, java.lang.String)}
   *
   * @param config config
   * @return {@link RouteFilter}
   */
  @Override
  public RouteFilter create(Config config) {
    return (request, response, chain) -> {
      List<String> headers = config.getHeaders();
      // TODO: cannot remove response header
      chain.doFilter(request, response);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private List<String> headers;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
