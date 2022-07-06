package com.lzhpo.panda.gateway.route;

import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import com.lzhpo.panda.gateway.filter.factory.RouteFilterFactory;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import com.lzhpo.panda.gateway.predicate.factory.RoutePredicateFactory;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * @author lzhpo
 */
public interface RouteLocator {

  /**
   * Get {@link Route} by routeId.
   *
   * @param routeId routeId
   * @return {@link Route}
   */
  Route getRoute(String routeId);

  /**
   * Get all {@link Route}
   *
   * @return all {@link Route}
   */
  List<Route> getRoutes();

  /**
   * Convert {@link RouteDefinition} to {@link Route}
   *
   * @param routeDefinition {@link RouteDefinition}
   * @return {@link Route}
   */
  default Route convert(RouteDefinition routeDefinition) {
    Route route = new Route();
    BeanUtils.copyProperties(routeDefinition, route);

    List<ComponentDefinition> predicateDefinitions = routeDefinition.getPredicates();
    List<RoutePredicate> predicates =
        predicateDefinitions.stream()
            .map(
                predicateDefinition -> {
                  String name = predicateDefinition.getName();
                  RoutePredicateFactory<Object> factory =
                      RouteComponentExtractor.getPredicateFactory(name);
                  return factory.create(predicateDefinition);
                })
            .collect(Collectors.toList());
    AnnotationAwareOrderComparator.sort(predicates);
    route.setPredicates(predicates);

    List<ComponentDefinition> filterDefinitions = routeDefinition.getFilters();
    List<RouteFilter> filters =
        filterDefinitions.stream()
            .map(
                filterDefinition -> {
                  String name = filterDefinition.getName();
                  RouteFilterFactory<Object> factory =
                      RouteComponentExtractor.getFilterFactory(name);
                  return factory.create(filterDefinition);
                })
            .collect(Collectors.toList());
    AnnotationAwareOrderComparator.sort(filters);
    route.setFilters(filters);
    return route;
  }
}
