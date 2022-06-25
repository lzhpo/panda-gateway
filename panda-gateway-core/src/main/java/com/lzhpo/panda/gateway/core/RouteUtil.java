package com.lzhpo.panda.gateway.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * @author lzhpo
 */
@UtilityClass
public class RouteUtil {

  public static <T> List<T> parseFilters(RouteDefinition route, Class<T> filterType) {
    String filterSuffixName = filterType.getSimpleName();
    return Optional.ofNullable(route)
        .map(RouteDefinition::getFilters)
        .map(
            filterDefinitions ->
                filterDefinitions.stream()
                    .map(ComponentDefinition::getName)
                    .map(StrUtil::lowerFirst)
                    .map(filterName -> filterName + filterSuffixName)
                    .map(SpringUtil::getBean)
                    .map(filterType::cast)
                    .collect(Collectors.toList()))
        .orElse(Lists.newArrayList());
  }

  public static <T> List<T> parsePredicates(RouteDefinition route, Class<T> predicateType) {
    String filterSuffixName = predicateType.getSimpleName();
    return Optional.ofNullable(route)
        .map(RouteDefinition::getPredicates)
        .map(
            filterDefinitions ->
                filterDefinitions.stream()
                    .map(ComponentDefinition::getName)
                    .map(StrUtil::lowerFirst)
                    .map(filterName -> filterName + filterSuffixName)
                    .map(SpringUtil::getBean)
                    .map(predicateType::cast)
                    .collect(Collectors.toList()))
        .orElse(Lists.newArrayList());
  }
}
