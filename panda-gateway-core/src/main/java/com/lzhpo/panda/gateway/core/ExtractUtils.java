package com.lzhpo.panda.gateway.core;

import cn.hutool.core.text.StrPool;
import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
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

  public static String stripPrefix(String path, int stripPrefix) {
    if (stripPrefix > 0) {
      CopyOnWriteArrayList<String> paths =
          Arrays.stream(path.split(StrPool.SLASH))
              .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
      while (stripPrefix > 0 && paths.size() > 0) {
        paths.remove(stripPrefix--);
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
