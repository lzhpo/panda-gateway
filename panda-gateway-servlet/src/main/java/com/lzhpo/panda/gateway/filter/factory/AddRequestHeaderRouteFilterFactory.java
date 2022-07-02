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
import java.util.Optional;
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
public class AddRequestHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<AddRequestHeaderRouteFilterFactory.Config>
    implements Ordered {

  public AddRequestHeaderRouteFilterFactory() {
    super(AddRequestHeaderRouteFilterFactory.Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (request, response, chain) ->
        chain.doFilter(modifyRequestIfNecessary(config, request), response);
  }

  private HttpServletRequestWrapper modifyRequestIfNecessary(
      Config config, HttpServletRequest request) {
    Map<String, String> configHeaders = new CaseInsensitiveMap<>(config.getHeaders());
    return new HttpServletRequestWrapper(request) {
      @Override
      public String getHeader(String name) {
        return Optional.ofNullable(configHeaders.get(name))
            .filter(StringUtils::hasText)
            .orElseGet(() -> super.getHeader(name));
      }

      @Override
      public Enumeration<String> getHeaders(String name) {
        Set<String> finalHeaders = new HashSet<>();

        Enumeration<String> originalHeaders = super.getHeaders(name);
        while (originalHeaders.hasMoreElements()) {
          String header = originalHeaders.nextElement();
          finalHeaders.add(header);
        }

        String configHeaderValue = configHeaders.get(name);
        if (Objects.nonNull(configHeaderValue)) {
          finalHeaders.add(configHeaderValue);
        }

        return Collections.enumeration(finalHeaders);
      }

      @Override
      public Enumeration<String> getHeaderNames() {
        List<String> finalHeaderNames = new ArrayList<>(configHeaders.keySet());
        Enumeration<String> headerNames = super.getHeaderNames();
        while (headerNames.hasMoreElements()) {
          String headerName = headerNames.nextElement();
          finalHeaderNames.add(headerName);
        }
        return Collections.enumeration(finalHeaderNames);
      }
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private Map<String, String> headers;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
