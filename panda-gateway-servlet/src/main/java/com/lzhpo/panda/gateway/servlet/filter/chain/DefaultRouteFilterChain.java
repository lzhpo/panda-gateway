package com.lzhpo.panda.gateway.servlet.filter.chain;

import com.lzhpo.panda.gateway.servlet.filter.RouteFilter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;

/**
 * @author lzhpo
 */
@Getter
public class DefaultRouteFilterChain implements RouteFilterChain {

  private final int index;
  private final List<RouteFilter> filters;

  public static DefaultRouteFilterChain create(List<RouteFilter> filters) {
    return new DefaultRouteFilterChain(filters);
  }

  private DefaultRouteFilterChain(List<RouteFilter> filters) {
    this.index = 0;
    this.filters = filters;
  }

  private DefaultRouteFilterChain(DefaultRouteFilterChain parent, int index) {
    this.filters = parent.getFilters();
    this.index = index;
  }

  @Override
  public void doFilter(HttpServletRequest request, HttpServletResponse response) {
    if (index < filters.size()) {
      RouteFilter filter = filters.get(index);
      DefaultRouteFilterChain chain = new DefaultRouteFilterChain(this, index + 1);
      filter.doFilter(request, response, chain);
    }
  }
}
