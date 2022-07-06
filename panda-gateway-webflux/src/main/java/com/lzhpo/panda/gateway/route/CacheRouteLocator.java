package com.lzhpo.panda.gateway.route;

import com.google.common.collect.Lists;
import com.lzhpo.panda.gateway.core.route.RouteRefreshEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class CacheRouteLocator implements RouteLocator, ApplicationListener<RouteRefreshEvent> {

  private final RouteDefinitionLocator routeDefinitionLocator;
  private final Map<String, Route> cacheRoutes = new ConcurrentHashMap<>();

  @Override
  public void onApplicationEvent(@NonNull RouteRefreshEvent event) {
    cacheRoutes.clear();
    routeDefinitionLocator
        .getRoutes()
        .toStream()
        .map(this::convert)
        .forEach(route -> cacheRoutes.put(route.getId(), route));
  }

  @Override
  public Route getRoute(String routeId) {
    return cacheRoutes.get(routeId);
  }

  @Override
  public List<Route> getRoutes() {
    return Lists.newArrayList(cacheRoutes.values()).stream()
        .sorted(Comparator.comparing(Route::getOrder))
        .collect(Collectors.toList());
  }
}
