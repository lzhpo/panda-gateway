package com.lzhpo.panda.gateway.filter.factory;

import cn.hutool.core.map.CaseInsensitiveMap;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
public class RemoveRequestHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<RemoveRequestHeaderRouteFilterFactory.Config>
    implements Ordered {

  public RemoveRequestHeaderRouteFilterFactory() {
    super(RemoveRequestHeaderRouteFilterFactory.Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (request, response, chain) ->
        chain.doFilter(modifyRequestIfNecessary(request, config), response);
  }

  private HttpServletRequestWrapper modifyRequestIfNecessary(
      HttpServletRequest request, Config config) {
    Map<String, String> configHeaders = new CaseInsensitiveMap<>(config.getHeaders());
    return new HttpServletRequestWrapper(request) {

      @Override
      public String getHeader(String name) {
        return !isMatch(name) ? super.getHeader(name) : null;
      }

      @Override
      public Enumeration<String> getHeaders(String name) {
        if (isMatch(name)) {
          return null;
        }

        Set<String> finalHeaders = new HashSet<>();
        Enumeration<String> headers = super.getHeaders(name);
        while (headers.hasMoreElements()) {
          String header = headers.nextElement();
          finalHeaders.add(header);
        }
        return Collections.enumeration(finalHeaders);
      }

      @Override
      public Enumeration<String> getHeaderNames() {
        List<String> finalHeaderNames = new ArrayList<>();
        Enumeration<String> headerNames = super.getHeaderNames();
        while (headerNames.hasMoreElements()) {
          String headerName = headerNames.nextElement();
          if (!isMatch(headerName)) {
            finalHeaderNames.add(headerName);
          }
        }
        return Collections.enumeration(finalHeaderNames);
      }

      private boolean isMatch(String name) {
        String requestHeaderValue = request.getHeader(name);
        String configHeaderRegexp = configHeaders.get(name);
        return StringUtils.hasText(configHeaderRegexp)
            && Objects.nonNull(requestHeaderValue)
            && requestHeaderValue.matches(configHeaderRegexp);
      }
    };
  }

  @Data
  @Validated
  public static class Config {

    /**
     * Request headers to delete
     *
     * <pre>
     * key: header name
     * value: regexp expression
     * </pre>
     */
    @NotEmpty private Map<String, String> headers;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
