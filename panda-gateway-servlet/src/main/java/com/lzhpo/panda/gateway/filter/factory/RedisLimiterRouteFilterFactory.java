package com.lzhpo.panda.gateway.filter.factory;

import com.google.common.collect.Lists;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
@Slf4j
public class RedisLimiterRouteFilterFactory
    extends AbstractRouteFilterFactory<RedisLimiterRouteFilterFactory.Config> implements Ordered {

  private final RedisScript<List<Long>> rateLimitRedisScript;
  private final StringRedisTemplate stringRedisTemplate;

  public RedisLimiterRouteFilterFactory(
      RedisScript<List<Long>> rateLimitRedisScript, StringRedisTemplate stringRedisTemplate) {
    super(RedisLimiterRouteFilterFactory.Config.class);
    this.rateLimitRedisScript = rateLimitRedisScript;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Override
  public RouteFilter create(Config config) {
    return (request, response, chain) -> {
      RouteDefinition route = (RouteDefinition) request.getAttribute(GatewayConst.ROUTE_DEFINITION);
      Assert.notNull(route, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

      String routeId = route.getId();
      String replenishRate = String.valueOf(config.getReplenishRate());
      String burstCapacity = String.valueOf(config.getBurstCapacity());
      String requestedTokens = String.valueOf(config.getRequestedTokens());

      List<String> keys = getKeys(routeId);
      List<String> scriptArgs = Lists.newArrayList(replenishRate, burstCapacity, requestedTokens);

      List<Long> executeResult =
          Optional.ofNullable(
                  stringRedisTemplate.execute(rateLimitRedisScript, keys, scriptArgs.toArray()))
              .orElseGet(
                  () -> {
                    log.error(
                        "rateLimitRedisScript cannot normal execute, please check! route: {}",
                        route);
                    return Lists.newArrayList(1L, -1L);
                  });

      boolean allowed = (executeResult.get(0) == 1L);
      Long tokensLeft = executeResult.get(1);

      if (config.isIncludeHeaders()) {
        response.setHeader(GatewayConst.REMAINING_HEADER, tokensLeft.toString());
        response.setHeader(GatewayConst.REPLENISH_RATE_HEADER, replenishRate);
        response.setHeader(GatewayConst.BURST_CAPACITY_HEADER, burstCapacity);
        response.setHeader(GatewayConst.REQUESTED_TOKENS_HEADER, requestedTokens);
      }

      if (!allowed) {
        limitedResponse(config, response);
        log.warn(
            "Request [{}] triggered limiter. keys: {}, scriptArgs: {}",
            request.getRequestURI(),
            keys,
            scriptArgs);
        return;
      }

      chain.doFilter(request, response);
    };
  }

  @SneakyThrows
  private void limitedResponse(Config config, HttpServletResponse response) {
    response.setStatus(config.getLimitedCode());
    ServletOutputStream outputStream = response.getOutputStream();
    outputStream.write(config.getLimitedMessage().getBytes());
    outputStream.flush();
    outputStream.close();
  }

  private List<String> getKeys(String id) {
    String prefix = "request_rate_limiter.{" + id;
    String tokenKey = prefix + "}.tokens";
    String timestampKey = prefix + "}.timestamp";
    return Arrays.asList(tokenKey, timestampKey);
  }

  @Data
  @Validated
  public static class Config {

    /** Whether to include headers containing rate limiter information, defaults is true. */
    private boolean includeHeaders = true;

    /** How many requests per second do you want a user to be allowed to do? */
    @Min(1)
    private int replenishRate;

    /** How much bursting do you want to allow? */
    @Min(1)
    private int burstCapacity = 1;

    /** How many tokens are requested per request? */
    @Min(1)
    private int requestedTokens = 1;

    /** Response status code if the request limited. */
    private int limitedCode = HttpStatus.TOO_MANY_REQUESTS.value();

    /** Response message if the request limited. */
    private String limitedMessage = HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase();
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
