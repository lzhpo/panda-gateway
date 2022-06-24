package com.lzhpo.panda.gateway.webflux.predicate;

import cn.hutool.core.text.StrPool;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;

/**
 * @author lzhpo
 */
public class PathServletPredicate implements ServletPredicate {

  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Override
  public String getPrefix() {
    String suffix = ServletPredicate.super.getSuffix();
    return getClass().getSimpleName().replace(suffix, "");
  }

  @Override
  public boolean apply(ServerHttpRequest request, RouteDefinition route) {
    String prefix = getPrefix();
    String requestPath = request.getPath().value();
    List<String> predicates = route.getPredicates();

    String[] patterns =
        predicates.stream()
            .filter(x -> x.startsWith(prefix))
            .map(x -> x.replace(prefix + "=", ""))
            .findAny()
            .map(x -> x.split(StrPool.COMMA))
            .orElse(null);

    return !ObjectUtils.isEmpty(patterns)
        && Arrays.stream(patterns).anyMatch(pattern -> antPathMatcher.match(pattern, requestPath));
  }
}
