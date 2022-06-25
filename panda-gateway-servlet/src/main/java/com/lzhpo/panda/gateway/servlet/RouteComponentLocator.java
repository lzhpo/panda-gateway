package com.lzhpo.panda.gateway.servlet;

import com.lzhpo.panda.gateway.servlet.filter.factory.FilterFactory;
import com.lzhpo.panda.gateway.servlet.filter.global.GlobalFilterInvoker;
import com.lzhpo.panda.gateway.servlet.filter.global.GlobalFilterInvokerAdapter;
import com.lzhpo.panda.gateway.servlet.predicate.PredicateInvoker;
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

  private final Map<String, PredicateInvoker> predicateFactories = new HashMap<>();
  private final Map<String, FilterFactory> filters = new HashMap<>();
  private final List<GlobalFilterInvokerAdapter> globalFilters = new ArrayList<>();

  public RouteComponentLocator(
      List<PredicateInvoker> predicateFactories,
      List<FilterFactory> filters,
      List<GlobalFilterInvoker> globalFilters) {

    predicateFactories.forEach(
        predicate -> this.predicateFactories.put(predicate.currentName(), predicate));
    filters.forEach(filter -> this.filters.put(filter.currentName(), filter));
    globalFilters.forEach(filter -> this.globalFilters.add(new GlobalFilterInvokerAdapter(filter)));
  }

  public PredicateInvoker getPredicate(String name) {
    return predicateFactories.get(name);
  }

  public FilterFactory getFilter(String name) {
    return filters.get(name);
  }

  public List<GlobalFilterInvokerAdapter> getGlobalFilters() {
    return globalFilters;
  }
}
