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

package com.lzhpo.panda.gateway.core.route;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author lzhpo
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GatewayConstants {

  public static final String ROUTE_ID = "ROUTE-ID";

  /** Route definitions cache key for redis */
  public static final String ROUTE_DEFINITION_CACHE_KEY = "route_definitions";

  /** Spring EL expression prefix */
  public static final String EXPRESSION_PREFIX = "#{";

  /** Spring EL expression suffix */
  public static final String EXPRESSION_SUFFIX = "}";

  /** Remaining Rate Limit header name. */
  public static final String REMAINING_HEADER = "X-RateLimit-Remaining";

  /** Replenish Rate Limit header name. */
  public static final String REPLENISH_RATE_HEADER = "X-RateLimit-Replenish-Rate";

  /** Burst Capacity header name. */
  public static final String BURST_CAPACITY_HEADER = "X-RateLimit-Burst-Capacity";

  /** Requested Tokens header name. */
  public static final String REQUESTED_TOKENS_HEADER = "X-RateLimit-Requested-Tokens";

  /** Http client connect timeout. Unit: milliseconds */
  public static final long DEFAULT_CONNECT_TIMEOUT = 60 * 1000L;

  /** Http client response timeout. Unit: milliseconds */
  public static final long DEFAULT_RESPONSE_TIMEOUT = 60 * 1000L;
}
