package com.lzhpo.panda.gateway.core;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.util.AntPathMatcher;

/**
 * @author lzhpo
 */
@UtilityClass
public class RouteUtil {

  private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

  public static <T> List<T> parseFilters(Route route, Class<T> filterType) {
    String filterSuffixName = filterType.getSimpleName();
    return Optional.ofNullable(route).map(Route::getFilters).orElse(Lists.newArrayList()).stream()
        .filter(Objects::nonNull)
        .map(x -> x.split("=")[0] + filterSuffixName)
        .map(StrUtil::lowerFirst)
        .map(SpringUtil::getBean)
        .map(filterType::cast)
        .collect(Collectors.toList());
  }

  public static <T> List<T> parsePredicates(Route route, Class<T> predicateType) {
    return Optional.ofNullable(route)
        .map(Route::getPredicates)
        .orElse(Lists.newArrayList())
        .stream()
        .map(x -> x.split("=")[0])
        .map(StrUtil::lowerFirst)
        .map(x -> x + predicateType.getSimpleName())
        .map(SpringUtil::getBean)
        .map(predicateType::cast)
        .collect(Collectors.toList());
  }

  public static boolean isMatch(Route route, String prefix, String requestPath) {
    return Optional.ofNullable(route)
        .map(Route::getPredicates)
        .orElse(Lists.newArrayList())
        .stream()
        .filter(x -> x.startsWith(prefix))
        .map(x -> x.replace(prefix + "=", ""))
        .findAny()
        .map(x -> x.split(StrPool.COMMA))
        .map(Arrays::asList)
        .orElse(Lists.newArrayList())
        .stream()
        .anyMatch(pattern -> ANT_PATH_MATCHER.match(pattern, requestPath));
  }
}
