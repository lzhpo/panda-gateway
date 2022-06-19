package com.lzhpo.panda.gateway.core;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * @author lzhpo
 */
@UtilityClass
public class ForwardTargetUtils {

  public static final String CLIENT_RESPONSE = "clientResponse";

  public static String stripPrefix(String requestPath, Integer stripPrefix) {
    if (Objects.nonNull(stripPrefix) && stripPrefix > 0) {
      int finallyStripPrefix = stripPrefix;
      List<String> requestPaths =
          Arrays.stream(requestPath.split("/")).collect(Collectors.toList());
      while (finallyStripPrefix > 0) {
        requestPaths.remove(finallyStripPrefix--);
      }
      requestPath = String.join("/", requestPaths);
    }
    return requestPath;
  }
}
