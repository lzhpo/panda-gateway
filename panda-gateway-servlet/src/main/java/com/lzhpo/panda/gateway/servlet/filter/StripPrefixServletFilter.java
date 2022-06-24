package com.lzhpo.panda.gateway.servlet.filter;

import com.lzhpo.panda.gateway.core.ExtractUtils;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.util.ObjectUtils;

/**
 * @author lzhpo
 */
public class StripPrefixServletFilter implements ServletFilter {

  @Override
  @SneakyThrows
  public void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain,
      RouteDefinition route) {

    String prefix = getPrefix();
    List<String> filters = route.getFilters();

    Integer stripPath =
        filters.stream()
            .filter(x -> x.startsWith(prefix))
            .map(x -> x.replace(prefix + "=", ""))
            .findAny()
            .map(Integer::parseInt)
            .orElse(null);

    if (!ObjectUtils.isEmpty(stripPath)) {
      HttpServletRequest newRequest = newRequest(request, stripPath);
      filterChain.doFilter(newRequest, response);
    }
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
