package com.lzhpo.panda.gateway.handler;

import com.lzhpo.panda.gateway.core.route.ComponentDefinition;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.filter.DefaultRouteFilterChain;
import com.lzhpo.panda.gateway.filter.ForwardRouteFilter;
import com.lzhpo.panda.gateway.filter.GlobalFilterAdapter;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import com.lzhpo.panda.gateway.route.RouteComponentUtil;
import com.lzhpo.panda.gateway.route.RouteDefinitionLocator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class GatewayRequestHandler implements WebHandler {

  private final WebClient.Builder webClientBuilder;
  private final RouteDefinitionLocator routeDefinitionLocator;

  @Override
  public Mono<Void> handle(ServerWebExchange exchange) {
    RouteDefinition route = exchange.getAttribute(GatewayConst.ROUTE_DEFINITION);
    List<GlobalFilterAdapter> globalFilterAdapters = RouteComponentUtil.getGlobalFilterAdapters();
    List<RouteFilter> filters = new ArrayList<>(globalFilterAdapters);

    if (!ObjectUtils.isEmpty(route)) {
      List<ComponentDefinition> filterDefinitions = route.getFilters();
      List<RouteFilter> routeFilters =
          filterDefinitions.stream()
              .map(
                  filterDefinition -> {
                    String filterName = filterDefinition.getName();
                    return Optional.ofNullable(RouteComponentUtil.getFilterFactory(filterName))
                        .map(filterFactory -> filterFactory.create(filterDefinition))
                        .orElseGet(
                            () -> {
                              log.error("Not found [{}] filterFactory.", filterName);
                              return null;
                            });
                  })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

      filters.addAll(routeFilters);
      filters.add(new ForwardRouteFilter(webClientBuilder));
    }

    AnnotationAwareOrderComparator.sort(filters);
    return new DefaultRouteFilterChain(filters).filter(exchange);
  }
}
