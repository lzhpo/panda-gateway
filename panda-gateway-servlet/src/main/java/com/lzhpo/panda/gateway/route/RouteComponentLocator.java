package com.lzhpo.panda.gateway.route;

import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.filter.GlobalFilter;
import com.lzhpo.panda.gateway.filter.GlobalFilterAdapter;
import com.lzhpo.panda.gateway.filter.factory.RouteFilterFactory;
import com.lzhpo.panda.gateway.predicate.factory.RoutePredicateFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.ObjectUtils;

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
  @SuppressWarnings({"unchecked"})
  default RoutePredicateFactory<Object> getPredicateFactory(String predicateFactoryName) {
    return SpringUtil.getBean(predicateFactoryName, RoutePredicateFactory.class);
  }

  /**
   * Use {@code filterFactoryName} to get route filter factory.
   *
   * @param filterFactoryName filter factory name
   * @return route filter factory
   */
  @SuppressWarnings({"unchecked"})
  default RouteFilterFactory<Object> getFilterFactory(String filterFactoryName) {
    return SpringUtil.getBean(filterFactoryName, RouteFilterFactory.class);
  }

  /**
   * Get all global filter adapters.
   *
   * @return all global filter adapters
   */
  default List<GlobalFilterAdapter> getGlobalFilterAdapters() {
    String[] names = SpringUtil.getBeanNamesForType(GlobalFilter.class);
    if (ObjectUtils.isEmpty(names)) {
      return Collections.emptyList();
    }

    return Arrays.stream(names)
        .map(name -> SpringUtil.getBean(name, GlobalFilter.class))
        .map(GlobalFilterAdapter::new)
        .collect(Collectors.toList());
  }
}
