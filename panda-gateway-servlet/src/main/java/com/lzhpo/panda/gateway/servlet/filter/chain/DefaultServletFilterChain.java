package com.lzhpo.panda.gateway.servlet.filter.chain;

import com.lzhpo.panda.gateway.servlet.filter.FilterInvoker;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;

/**
 * @author lzhpo
 */
@Getter
public class DefaultServletFilterChain implements FilterInvokerChain {

  private final int index;
  private final List<FilterInvoker> filters;

  public static DefaultServletFilterChain create(List<FilterInvoker> filters) {
    return new DefaultServletFilterChain(filters);
  }

  private DefaultServletFilterChain(List<FilterInvoker> filters) {
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
      FilterInvoker filter = filters.get(index);
      DefaultServletFilterChain chain = new DefaultServletFilterChain(this, index + 1);
      filter.doFilter(request, response, chain);
    }
  }
}
