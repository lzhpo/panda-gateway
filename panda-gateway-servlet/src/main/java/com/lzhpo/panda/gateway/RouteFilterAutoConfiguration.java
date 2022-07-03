package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.filter.factory.AddRequestHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.AddRequestParameterRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.AddResponseHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.RedisLimiterRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.RemoveRequestHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.RemoveRequestParameterRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.RemoveResponseHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.StripPrefixRouteFilterFactory;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * @author lzhpo
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
public class RouteFilterAutoConfiguration {

  @Bean
  @ConditionalOnProperty(prefix = "gateway.redis", value = "enabled", havingValue = "true")
  public RedisLimiterRouteFilterFactory redisLimiterRouteFilterFactory(
      RedisScript<List<Long>> rateLimitRedisScript, StringRedisTemplate stringRedisTemplate) {
    return new RedisLimiterRouteFilterFactory(rateLimitRedisScript, stringRedisTemplate);
  }

  @Bean
  public AddRequestParameterRouteFilterFactory addRequestParameterRouteFilterFactory() {
    return new AddRequestParameterRouteFilterFactory();
  }

  @Bean
  public RemoveRequestParameterRouteFilterFactory removeRequestParameterRouteFilterFactory() {
    return new RemoveRequestParameterRouteFilterFactory();
  }

  @Bean
  public StripPrefixRouteFilterFactory stripPrefixFilterFactory() {
    return new StripPrefixRouteFilterFactory();
  }

  @Bean
  public AddRequestHeaderRouteFilterFactory addRequestHeaderRouteFilterFactory() {
    return new AddRequestHeaderRouteFilterFactory();
  }

  @Bean
  public RemoveRequestHeaderRouteFilterFactory removeRequestHeaderRouteFilterFactory() {
    return new RemoveRequestHeaderRouteFilterFactory();
  }

  @Bean
  public AddResponseHeaderRouteFilterFactory addResponseHeaderRouteFilterFactory() {
    return new AddResponseHeaderRouteFilterFactory();
  }

  @Bean
  public RemoveResponseHeaderRouteFilterFactory removeResponseHeaderRouteFilterFactory() {
    return new RemoveResponseHeaderRouteFilterFactory();
  }
}
