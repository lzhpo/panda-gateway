/*
 * Copyright 2022 lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzhpo.panda.gateway.core.utils;

import cn.hutool.core.text.StrPool;
import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

/**
 * @author lzhpo
 */
@UtilityClass
public class ExtractUtil {

  public static String mapToParamPath(Map<String, String> paramsMap) {
    return Joiner.on("&").useForNull("").withKeyValueSeparator("=").join(paramsMap);
  }

  public static String stripPrefix(String path, int stripPrefix) {
    if (stripPrefix > 0) {
      CopyOnWriteArrayList<String> paths =
          Arrays.stream(path.split(StrPool.SLASH))
              .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
      while (stripPrefix > 0 && !CollectionUtils.isEmpty(paths)) {
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
