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
