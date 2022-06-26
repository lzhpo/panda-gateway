package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.filter.GlobalFilterAdapter;
import com.lzhpo.panda.gateway.filter.factory.RouteFilterFactory;
import com.lzhpo.panda.gateway.predicate.factory.RoutePredicateFactory;
import java.util.List;

/**
 * @author lzhpo
 */
public interface RouteComponentLocator {

  /**
   * Use {@code predicateFactoryName} to get route predicate factory.
   *
   * @param predicateFactoryName predicate factory name
   * @return route predicate factory
   */
  RoutePredicateFactory<Object> getPredicateFactory(String predicateFactoryName);

  /**
   * Use {@code filterFactoryName} to get route filter factory.
   *
   * @param filterFactoryName filter factory name
   * @return route filter factory
   */
  RouteFilterFactory<Object> getFilterFactory(String filterFactoryName);

  /**
   * Get all global filter adapters.
   *
   * @return all global filter adapters
   */
  List<GlobalFilterAdapter> getGlobalFilterAdapters();
}
