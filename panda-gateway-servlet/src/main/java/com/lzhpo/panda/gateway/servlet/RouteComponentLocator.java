package com.lzhpo.panda.gateway.servlet;

import com.lzhpo.panda.gateway.servlet.filter.GlobalServletFilter;
import com.lzhpo.panda.gateway.servlet.filter.ServletFilter;
import com.lzhpo.panda.gateway.servlet.filter.support.GlobalServletFilterAdapter;
import com.lzhpo.panda.gateway.servlet.predicate.RoutePredicateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzhpo
 */
@Slf4j
public class RouteComponentLocator {

  private final Map<String, RoutePredicateFactory> predicateFactories = new HashMap<>();
  private final Map<String, ServletFilter> filters = new HashMap<>();
  private final List<GlobalServletFilterAdapter> globalFilters = new ArrayList<>();

  public RouteComponentLocator(
      List<RoutePredicateFactory> predicateFactories,
      List<ServletFilter> filters,
      List<GlobalServletFilter> globalFilters) {

    predicateFactories.forEach(
        predicate -> this.predicateFactories.put(predicate.getName(), predicate));
    filters.forEach(filter -> this.filters.put(filter.getName(), filter));
    globalFilters.forEach(filter -> this.globalFilters.add(new GlobalServletFilterAdapter(filter)));
  }

  public RoutePredicateFactory getPredicate(String name) {
    return predicateFactories.get(name);
  }

  public ServletFilter getFilter(String name) {
    return filters.get(name);
  }

  public List<GlobalServletFilterAdapter> getGlobalFilters() {
    return globalFilters;
  }
}
