package com.lzhpo.panda.gateway.servlet.filter;

import com.lzhpo.panda.gateway.core.ExtractUtils;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import lombok.SneakyThrows;
import org.springframework.util.ObjectUtils;

/**
 * @author lzhpo
 */
public class StripPrefixServletFilter implements ServletFilter {

  @Override
  @SneakyThrows
  public HttpServletRequest doFilterInternal(HttpServletRequest request, RouteDefinition route) {

    String prefix = getPrefix();
    List<String> filters = route.getFilters();

    Integer stripPath =
        filters.stream()
            .filter(x -> x.startsWith(prefix))
            .map(x -> x.replace(prefix + "=", ""))
            .findAny()
            .map(Integer::parseInt)
            .orElse(null);

    return !ObjectUtils.isEmpty(stripPath) ? newRequest(request, stripPath) : request;
  }

  private HttpServletRequest newRequest(HttpServletRequest request, Integer stripPath) {
    return new HttpServletRequestWrapper(request) {

      @Override
      public String getRequestURI() {
        String requestUri = super.getRequestURI();
        return ExtractUtils.stripPrefix(requestUri, stripPath);
      }
    };
  }

  @Override
  public String getPrefix() {
    return getClass().getSimpleName().replace(getSuffix(), "");
  }
}
