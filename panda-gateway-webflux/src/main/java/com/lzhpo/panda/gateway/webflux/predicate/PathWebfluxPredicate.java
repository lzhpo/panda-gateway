package com.lzhpo.panda.gateway.webflux.predicate;

import cn.hutool.core.text.StrPool;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author lzhpo
 */
public class PathWebfluxPredicate implements WebfluxPredicate {

  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Override
  public String getPrefix() {
    String suffix = WebfluxPredicate.super.getSuffix();
    return getClass().getSimpleName().replace(suffix, "");
  }

  @Override
  public boolean apply(ServerWebExchange exchange, RouteDefinition route) {
    String prefix = getPrefix();
    ServerHttpRequest request = exchange.getRequest();
    String requestPath = request.getPath().value();

    List<String> patterns =
        route.getPredicates().stream()
            .filter(x -> x.startsWith(prefix))
            .map(x -> x.replace(prefix + "=", ""))
            .findAny()
            .map(x -> x.split(StrPool.COMMA))
            .map(Arrays::asList)
            .orElse(null);

    return !CollectionUtils.isEmpty(patterns)
        && patterns.stream().anyMatch(pattern -> antPathMatcher.match(pattern, requestPath));
  }
}
