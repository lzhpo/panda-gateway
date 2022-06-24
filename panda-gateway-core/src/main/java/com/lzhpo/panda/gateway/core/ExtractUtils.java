package com.lzhpo.panda.gateway.core;

import cn.hutool.core.text.StrPool;
import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpMethod;

/**
 * @author lzhpo
 */
@UtilityClass
public class ExtractUtils {

  public static String mapToParamPath(Map<String, String> paramsMap) {
    return Joiner.on("&").useForNull("").withKeyValueSeparator("=").join(paramsMap);
  }

  public static String stripPrefix(String path, Integer stripPrefix) {
    if (Objects.nonNull(stripPrefix) && stripPrefix > 0) {
      int finallyStripPrefix = stripPrefix;
      List<String> paths = Arrays.stream(path.split(StrPool.SLASH)).collect(Collectors.toList());
      while (finallyStripPrefix > 0) {
        paths.remove(finallyStripPrefix--);
      }
      path = String.join(StrPool.SLASH, paths);
    }
    return path;
  }

  public static boolean requireBody(HttpMethod method) {
    switch (method) {
      case PUT:
      case POST:
      case PATCH:
        return true;
      default:
        return false;
    }
  }
}
