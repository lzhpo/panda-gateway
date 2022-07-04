package com.lzhpo.panda.gateway.support;

import com.google.common.collect.Lists;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.filter.factory.RateLimiterRouteFilterFactory.Config;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * Redis rate limiter
 *
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class RedisRateLimiter implements RateLimiter {

  private final RedisScript<List<Long>> rateLimitRedisScript;
  private final StringRedisTemplate redisTemplate;

  @Override
  public RateLimiterResponse isAllowed(Config config, String id) {
    String replenishRate = String.valueOf(config.getReplenishRate());
    String burstCapacity = String.valueOf(config.getBurstCapacity());
    String requestedTokens = String.valueOf(config.getRequestedTokens());

    List<String> keys = getKeys(id);
    List<String> scriptArgs = Lists.newArrayList(replenishRate, burstCapacity, requestedTokens);

    List<Long> executeResult =
        Optional.ofNullable(
                redisTemplate.execute(rateLimitRedisScript, keys, scriptArgs.toArray()))
            .orElseGet(
                () -> {
                  log.error("rateLimitRedisScript cannot normal execute, please check! id: {}", id);
                  return Lists.newArrayList(1L, -1L);
                });

    boolean allowed = (executeResult.get(0) == 1L);
    Long tokensLeft = executeResult.get(1);

    Map<String, String> headers = new HashMap<>(4);
    if (config.isIncludeHeaders()) {
      headers.put(GatewayConst.REMAINING_HEADER, tokensLeft.toString());
      headers.put(GatewayConst.REPLENISH_RATE_HEADER, replenishRate);
      headers.put(GatewayConst.BURST_CAPACITY_HEADER, burstCapacity);
      headers.put(GatewayConst.REQUESTED_TOKENS_HEADER, requestedTokens);
    }

    return RateLimiterResponse.builder()
        .allowed(allowed)
        .tokensLeft(tokensLeft)
        .headers(headers)
        .build();
  }

  private List<String> getKeys(String id) {
    String prefix = "request_rate_limiter.{" + id;
    String tokenKey = prefix + "}.tokens";
    String timestampKey = prefix + "}.timestamp";
    return Arrays.asList(tokenKey, timestampKey);
  }
}
