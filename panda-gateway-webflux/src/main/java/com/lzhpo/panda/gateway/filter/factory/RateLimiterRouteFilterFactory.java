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

package com.lzhpo.panda.gateway.filter.factory;

import com.lzhpo.panda.gateway.filter.RouteFilter;
import com.lzhpo.panda.gateway.support.KeyResolver;
import com.lzhpo.panda.gateway.support.RateLimiter;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;

/**
 * @author lzhpo
 */
@Slf4j
public class RateLimiterRouteFilterFactory
    extends AbstractRouteFilterFactory<RateLimiterRouteFilterFactory.Config> implements Ordered {

  public RateLimiterRouteFilterFactory() {
    super(Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (exchange, filterChain) -> {
      KeyResolver keyResolver = config.getKeyResolver();
      RateLimiter rateLimiter = config.getRateLimiter();

      return keyResolver
          .resolve(exchange)
          .flatMap(key -> rateLimiter.isAllowed(config, key))
          .flatMap(
              limiterResponse -> {
                if (limiterResponse.isAllowed()) {
                  return filterChain.filter(exchange);
                }

                ServerHttpResponse httpResponse = exchange.getResponse();
                httpResponse.setStatusCode(HttpStatus.resolve(config.getLimitedCode()));
                byte[] messageBytes = config.getLimitedMessage().getBytes();
                DataBuffer messageBuffer = httpResponse.bufferFactory().wrap(messageBytes);
                return httpResponse
                    .writeWith(Flux.just(messageBuffer))
                    .doFinally(
                        x ->
                            log.warn(
                                "Request [{}] triggered limiter, response: {}",
                                exchange.getRequest().getPath().value(),
                                limiterResponse));
              });
    };
  }

  @Data
  @Validated
  public static class Config {

    /**
     * {@link RateLimiter} bean name.
     *
     * <p>Support spring el expression, e.g: {@code "#{@clientIpKeyResolver}"}
     */
    @NotNull private RateLimiter rateLimiter;

    /**
     * {@link KeyResolver} bean name.
     *
     * <p>Support spring el expression, e.g: {@code "#{@redisRateLimiter}"}
     */
    @NotNull private KeyResolver keyResolver;

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
    @NotBlank private String limitedMessage = HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase();
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
