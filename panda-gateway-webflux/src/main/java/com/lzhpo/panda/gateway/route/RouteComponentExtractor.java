package com.lzhpo.panda.gateway.route;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.filter.GlobalFilter;
import com.lzhpo.panda.gateway.filter.GlobalFilterAdapter;
import com.lzhpo.panda.gateway.filter.factory.RouteFilterFactory;
import com.lzhpo.panda.gateway.predicate.factory.RoutePredicateFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.util.ObjectUtils;

/**
 * @author lzhpo
 */
@UtilityClass
public class RouteComponentExtractor {

  /**
   * Use {@code name} to get route predicate factory.
   *
   * @param name predicate factory name
   * @return route predicate factory
   */
  @SuppressWarnings({"unchecked"})
  public static RoutePredicateFactory<Object> getPredicateFactory(String name) {
    String suffix = RoutePredicateFactory.class.getSimpleName();
    if (!name.endsWith(suffix)) {
      name += suffix;
    }
    return SpringUtil.getBean(CharSequenceUtil.lowerFirst(name), RoutePredicateFactory.class);
  }

  /**
   * Use {@code name} to get route filter factory.
   *
   * @param name filter factory name
   * @return route filter factory
   */
  @SuppressWarnings({"unchecked"})
  public static RouteFilterFactory<Object> getFilterFactory(String name) {
    String suffix = RouteFilterFactory.class.getSimpleName();
    if (!name.endsWith(suffix)) {
      name += suffix;
    }
    return SpringUtil.getBean(CharSequenceUtil.lowerFirst(name), RouteFilterFactory.class);
  }

  /**
   * Get all global filter adapters.
   *
   * @return all global filter adapters
   */
  public static List<GlobalFilterAdapter> getGlobalFilterAdapters() {
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
