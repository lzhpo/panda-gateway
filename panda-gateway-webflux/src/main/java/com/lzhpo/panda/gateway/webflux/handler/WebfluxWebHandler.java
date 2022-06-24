package com.lzhpo.panda.gateway.webflux.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.consts.GatewayConst;
import com.lzhpo.panda.gateway.webflux.filter.DefaultWebfluxFilterChain;
import com.lzhpo.panda.gateway.webflux.filter.ForwardGlobalWebfluxFilter;
import com.lzhpo.panda.gateway.webflux.filter.GlobalWebfluxFilter;
import com.lzhpo.panda.gateway.webflux.filter.GlobalWebfluxFilterAdapter;
import com.lzhpo.panda.gateway.webflux.filter.WebfluxFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class WebfluxWebHandler implements WebHandler {

  private final WebClient webClient;
  private final List<GlobalWebfluxFilter> globalFilters;

  @Override
  public Mono<Void> handle(ServerWebExchange exchange) {
    RouteDefinition route = exchange.getAttribute(GatewayConst.ROUTE_DEFINITION);
    List<WebfluxFilter> filters = new ArrayList<>();
    globalFilters.stream().map(GlobalWebfluxFilterAdapter::new).forEach(filters::add);

    if (Objects.nonNull(route)) {
      String webfluxFilterName = WebfluxFilter.class.getSimpleName();

      List<WebfluxFilter> routeFilters =
          route.getFilters().stream()
              .filter(Objects::nonNull)
              .map(x -> x.split("=")[0] + webfluxFilterName)
              .map(StrUtil::lowerFirst)
              .map(SpringUtil::getBean)
              .map(WebfluxFilter.class::cast)
              .collect(Collectors.toList());

      filters.addAll(routeFilters);
      AnnotationAwareOrderComparator.sort(filters);
      ForwardGlobalWebfluxFilter forwardFilter = new ForwardGlobalWebfluxFilter(webClient);
      filters.add(new GlobalWebfluxFilterAdapter(forwardFilter));
    }

    return new DefaultWebfluxFilterChain(filters).filter(exchange);
  }
}
