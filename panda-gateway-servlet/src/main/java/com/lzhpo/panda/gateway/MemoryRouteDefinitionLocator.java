package com.lzhpo.panda.gateway;

import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.filter.GlobalFilter;
import com.lzhpo.panda.gateway.filter.GlobalFilterAdapter;
import com.lzhpo.panda.gateway.filter.factory.RouteFilterFactory;
import com.lzhpo.panda.gateway.predicate.factory.RoutePredicateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzhpo
 */
@Slf4j
public class MemoryRouteDefinitionLocator implements RouteDefinitionLocator {

  private final List<RouteDefinition> routeDefinitions;
  private final Map<String, RoutePredicateFactory<Object>> predicateFactories = new HashMap<>();
  private final Map<String, RouteFilterFactory<Object>> filterFactories = new HashMap<>();
  private final List<GlobalFilterAdapter> globalFilterAdapters = new ArrayList<>();

  /**
   * Init route definitions and global filter adapters
   *
   * @param routeDefinitions route definitions
   * @param globalFilters global filter adapters
   */
  public MemoryRouteDefinitionLocator(
      List<RouteDefinition> routeDefinitions, List<GlobalFilter> globalFilters) {
    validateRoute(routeDefinitions);
    this.routeDefinitions = sortRoutes(routeDefinitions);
    globalFilters.forEach(filter -> this.globalFilterAdapters.add(new GlobalFilterAdapter(filter)));
    log.info("Found {} of GlobalFilter.", globalFilters.size());
  }

  /** Init route components, eg: predicate factory, filter factory */
  @PostConstruct
  public void initComponents() {
    String[] predicateFactoryNames = SpringUtil.getBeanNamesForType(RoutePredicateFactory.class);
    log.info("Found {} of RoutePredicateFactory.", predicateFactoryNames.length);
    for (String predicateFactoryName : predicateFactoryNames) {
      RoutePredicateFactory<Object> predicateFactory = SpringUtil.getBean(predicateFactoryName);
      predicateFactories.put(predicateFactory.currentName(), predicateFactory);
    }

    String[] filterFactoryNames = SpringUtil.getBeanNamesForType(RouteFilterFactory.class);
    log.info("Found {} of RouteFilterFactory.", filterFactoryNames.length);
    for (String filterFactoryName : filterFactoryNames) {
      RouteFilterFactory<Object> filterFactory = SpringUtil.getBean(filterFactoryName);
      filterFactories.put(filterFactory.currentName(), filterFactory);
    }
  }

  @Override
  public RouteDefinition getRoute(String routeId) {
    return routeDefinitions.stream().filter(x -> x.getId().equals(routeId)).findAny().orElse(null);
  }

  @Override
  public List<RouteDefinition> getRoutes() {
    return routeDefinitions;
  }

  @Override
  public RoutePredicateFactory<Object> getPredicateFactory(String predicateFactoryName) {
    return predicateFactories.get(predicateFactoryName);
  }

  @Override
  public RouteFilterFactory<Object> getFilterFactory(String filterFactoryName) {
    return filterFactories.get(filterFactoryName);
  }

  @Override
  public List<GlobalFilterAdapter> getGlobalFilterAdapters() {
    return globalFilterAdapters;
  }
}
