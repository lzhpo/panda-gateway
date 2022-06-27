package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.ConfigFactory;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Just extends it if you want implements route filter.
 *
 * @author lzhpo
 */
public abstract class AbstractRouteFilterFactory<T>
    implements ConfigFactory<T>, RouteFilterFactory<T> {

  protected final Class<T> configClass;
  private final Map<String, RouteFilter> routeFilters = new HashMap<>();

  public AbstractRouteFilterFactory(Class<T> configClass) {
    this.configClass = configClass;
  }

  @Override
  public Class<T> getConfigClass() {
    return configClass;
  }

  @Override
  public RouteFilter create(ComponentDefinition componentDefinition) {
    String predicateName = componentDefinition.getName();
    return Optional.ofNullable(routeFilters.get(predicateName))
        .orElseGet(
            () -> {
              RouteFilter routeFilter = RouteFilterFactory.super.create(componentDefinition);
              routeFilters.put(predicateName, routeFilter);
              return routeFilter;
            });
  }
}
