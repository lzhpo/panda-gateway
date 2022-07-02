package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.core.utils.ExtractUtil;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.validation.constraints.Min;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class StripPrefixRouteFilterFactory
    extends AbstractRouteFilterFactory<StripPrefixRouteFilterFactory.Config> implements Ordered {

  public StripPrefixRouteFilterFactory() {
    super(StripPrefixRouteFilterFactory.Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (request, response, chain) -> chain.doFilter(newRequest(request, config), response);
  }

  @Data
  @Validated
  public static class Config {

    @Min(1)
    private int parts;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  private HttpServletRequest newRequest(HttpServletRequest request, Config config) {
    return new HttpServletRequestWrapper(request) {

      @Override
      public String getRequestURI() {
        return ExtractUtil.stripPrefix(super.getRequestURI(), config.getParts());
      }
    };
  }
}
