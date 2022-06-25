package com.lzhpo.panda.gateway.servlet;

import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.servlet.filter.factory.RouteFilterFactory;
import com.lzhpo.panda.gateway.servlet.filter.global.GlobalFilter;
import com.lzhpo.panda.gateway.servlet.filter.global.GlobalFilterAdapter;
import com.lzhpo.panda.gateway.servlet.predicate.factory.RoutePredicateFactory;
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
public class RouteComponentLocator {

  private final Map<String, RoutePredicateFactory<Object>> predicateFactories = new HashMap<>();
  private final Map<String, RouteFilterFactory<Object>> filterFactories = new HashMap<>();
  private final List<GlobalFilterAdapter> globalFilterAdapters = new ArrayList<>();

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

  public RouteComponentLocator(List<GlobalFilter> globalFilterAdapters) {
    globalFilterAdapters.forEach(
        filter -> this.globalFilterAdapters.add(new GlobalFilterAdapter(filter)));
    log.info("Found {} of GlobalFilter.", globalFilterAdapters.size());
  }

  public RoutePredicateFactory<Object> getPredicateFactory(String predicateFactoryName) {
    return predicateFactories.get(predicateFactoryName);
  }

  public RouteFilterFactory<Object> getFilterFactory(String filterFactoryName) {
    return filterFactories.get(filterFactoryName);
  }

  public List<GlobalFilterAdapter> getGlobalFilterAdapters() {
    return globalFilterAdapters;
  }
}
