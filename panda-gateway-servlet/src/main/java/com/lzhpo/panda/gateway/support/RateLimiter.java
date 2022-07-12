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

package com.lzhpo.panda.gateway.support;

import com.lzhpo.panda.gateway.filter.factory.RateLimiterRouteFilterFactory.Config;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Rate limiter
 *
 * @author lzhpo
 */
public interface RateLimiter {

  /**
   * Resolve key
   *
   * @param config {@link Config}
   * @param id id
   * @return {@link RateLimiterResponse}
   */
  RateLimiterResponse isAllowed(Config config, String id);

  /** RateLimiter response */
  @Data
  @Builder
  class RateLimiterResponse {

    /** Whether to allow this request pass? */
    private boolean allowed;

    /** How many tokens are left in the token bucket? */
    private long tokensLeft;

    /** The data that need to be set on the response headers. */
    private Map<String, String> headers;
  }
}
