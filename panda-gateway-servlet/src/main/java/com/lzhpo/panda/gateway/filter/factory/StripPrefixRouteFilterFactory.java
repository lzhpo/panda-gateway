package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.core.ExtractUtils;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import lombok.Data;
import org.springframework.core.Ordered;

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
    return (request, response, chain) ->
        chain.doFilter(newRequest(request, config.getParts()), response);
  }

  @Data
  public static class Config {

    private int parts;
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  private HttpServletRequest newRequest(HttpServletRequest request, Integer parts) {
    return new HttpServletRequestWrapper(request) {

      @Override
      public String getRequestURI() {
        return ExtractUtils.stripPrefix(super.getRequestURI(), parts);
      }
    };
  }
}
