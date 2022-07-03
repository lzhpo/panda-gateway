package com.lzhpo.panda.gateway.filter.factory;

import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.core.route.GatewayConst;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import com.lzhpo.panda.gateway.support.KeyResolver;
import com.lzhpo.panda.gateway.support.RateLimiter;
import com.lzhpo.panda.gateway.support.RateLimiter.RateLimiterResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.Ordered;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
@Slf4j
public class RedisLimiterRouteFilterFactory
    extends AbstractRouteFilterFactory<RedisLimiterRouteFilterFactory.Config> implements Ordered {

  private static final TemplateParserContext PARSER_CONTEXT = new TemplateParserContext();
  private static final SpelExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
  private static final StandardEvaluationContext EVALUATION_CONTEXT =
      new StandardEvaluationContext();

  static {
    EVALUATION_CONTEXT.setBeanResolver(new BeanFactoryResolver(SpringUtil.getBeanFactory()));
  }

  public RedisLimiterRouteFilterFactory() {
    super(RedisLimiterRouteFilterFactory.Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (request, response, chain) -> {
      RouteDefinition route = (RouteDefinition) request.getAttribute(GatewayConst.ROUTE_DEFINITION);
      Assert.notNull(route, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

      KeyResolver keyResolver = getKeyResolver(config);
      RateLimiter rateLimiter = getRateLimiter(config);

      String key = keyResolver.resolve(request);
      RateLimiterResponse limiterResponse = rateLimiter.isAllowed(config, key);

      if (!limiterResponse.isAllowed()) {
        limitedResponse(config, response);
        log.warn(
            "Request [{}] triggered limiter, response: {}",
            request.getRequestURI(),
            limiterResponse);
        return;
      }

      chain.doFilter(request, response);
    };
  }

  private RateLimiter getRateLimiter(Config config) {
    Expression rateLimiterExpression =
        EXPRESSION_PARSER.parseExpression(config.getRateLimiter(), PARSER_CONTEXT);
    RateLimiter rateLimiter = rateLimiterExpression.getValue(EVALUATION_CONTEXT, RateLimiter.class);
    Assert.notNull(rateLimiter, "rateLimiter cannot null");
    return rateLimiter;
  }

  private KeyResolver getKeyResolver(Config config) {
    Expression keyResolverExpression =
        EXPRESSION_PARSER.parseExpression(config.getKeyResolver(), PARSER_CONTEXT);
    KeyResolver keyResolver = keyResolverExpression.getValue(EVALUATION_CONTEXT, KeyResolver.class);
    Assert.notNull(keyResolver, "keyResolver cannot null");
    return keyResolver;
  }

  @SneakyThrows
  private void limitedResponse(Config config, HttpServletResponse response) {
    response.setStatus(config.getLimitedCode());
    ServletOutputStream outputStream = response.getOutputStream();
    outputStream.write(config.getLimitedMessage().getBytes());
    outputStream.flush();
    outputStream.close();
  }

  @Data
  @Validated
  public static class Config {

    /**
     * {@link RateLimiter} bean name.
     *
     * <p>Support spring el expression, e.g: {@code "#{@clientIpKeyResolver}"}
     */
    @NotBlank private String rateLimiter;

    /**
     * {@link KeyResolver} bean name.
     *
     * <p>Support spring el expression, e.g: {@code "#{@redisRateLimiter}"}
     */
    @NotBlank private String keyResolver;

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
