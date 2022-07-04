package com.lzhpo.panda.gateway.core.route;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author lzhpo
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GatewayConst {

  /** Route definition attribute name or redis cache key prefix */
  public static final String ROUTE_DEFINITION = "route_definition";

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
}
