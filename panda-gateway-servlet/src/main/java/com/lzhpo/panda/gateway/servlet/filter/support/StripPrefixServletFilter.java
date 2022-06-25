package com.lzhpo.panda.gateway.servlet.filter.support;

import com.google.common.collect.Lists;
import com.lzhpo.panda.gateway.core.ExtractUtils;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.servlet.filter.ServletFilter;
import com.lzhpo.panda.gateway.servlet.filter.ServletFilterChain;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.Ordered;

/**
 * @author lzhpo
 */
public class StripPrefixServletFilter implements ServletFilter, Ordered {

  @Data
  @AllArgsConstructor
  public static class Config {

    private int parts;
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  @Override
  public void filter(
      HttpServletRequest request, HttpServletResponse response, ServletFilterChain filterChain) {

    String currentFilterName = getName();
    getRoute(request).map(RouteDefinition::getFilters).orElse(Lists.newArrayList()).stream()
        .filter(x -> x.getName().equalsIgnoreCase(currentFilterName))
        .findAny()
        .ifPresentOrElse(
            predicateDefinition -> {
              Map<String, String> args = predicateDefinition.getArgs();
              System.out.println(args);
              filterChain.doFilter(newRequest(request, 2), response);
            },
            () -> filterChain.doFilter(request, response));
  }

  private HttpServletRequest newRequest(HttpServletRequest request, Integer value) {
    return new HttpServletRequestWrapper(request) {

      @Override
      public String getRequestURI() {
        String requestUri = super.getRequestURI();
        return ExtractUtils.stripPrefix(requestUri, value);
      }
    };
  }
}
