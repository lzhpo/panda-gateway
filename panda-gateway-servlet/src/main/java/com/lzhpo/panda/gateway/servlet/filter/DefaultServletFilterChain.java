package com.lzhpo.panda.gateway.servlet.filter;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;

/**
 * @author lzhpo
 */
@Getter
public class DefaultServletFilterChain implements ServletFilterChain {

  private final int index;
  private final List<ServletFilter> filters;

  public static DefaultServletFilterChain create(List<ServletFilter> filters) {
    return new DefaultServletFilterChain(filters);
  }

  private DefaultServletFilterChain(List<ServletFilter> filters) {
    this.index = 0;
    this.filters = filters;
  }

  private DefaultServletFilterChain(DefaultServletFilterChain parent, int index) {
    this.filters = parent.getFilters();
    this.index = index;
  }

  @Override
  public void doFilter(HttpServletRequest request, HttpServletResponse response) {
    if (index < filters.size()) {
      ServletFilter filter = filters.get(index);
      DefaultServletFilterChain chain = new DefaultServletFilterChain(this, index + 1);
      filter.filter(request, response, chain);
    }
  }
}
