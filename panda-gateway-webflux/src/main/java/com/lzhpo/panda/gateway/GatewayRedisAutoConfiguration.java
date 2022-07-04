package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author lzhpo
 */
@Configuration
@ConditionalOnWebApplication(type = Type.REACTIVE)
@ConditionalOnClass({RedisScript.class, ReactiveStringRedisTemplate.class})
@ConditionalOnProperty(prefix = "gateway.redis", value = "enabled", havingValue = "true")
public class GatewayRedisAutoConfiguration {

  @Bean
  @SuppressWarnings({"unchecked", "rawtypes"})
  public RedisScript<List<Long>> rateLimitRedisScript() {
    DefaultRedisScript redisScript = new DefaultRedisScript<>();
    ClassPathResource resource = new ClassPathResource("META-INF/scripts/request_rate_limiter.lua");
    redisScript.setScriptSource(new ResourceScriptSource(resource));
    redisScript.setResultType(List.class);
    return redisScript;
  }

  @Bean
  @ConditionalOnMissingBean
  public ReactiveStringRedisTemplate reactiveStringRedisTemplate(
      ReactiveRedisConnectionFactory connectionFactory) {
    return new ReactiveStringRedisTemplate(connectionFactory);
  }

  @Bean
  public ReactiveRedisTemplate<String, RouteDefinition> reactiveRedisTemplate(
      ReactiveRedisConnectionFactory connectionFactory) {
    RedisSerializer<String> keySerializer = RedisSerializer.string();
    Jackson2JsonRedisSerializer<RouteDefinition> valueSerializer =
        new Jackson2JsonRedisSerializer<>(RouteDefinition.class);
    RedisSerializationContext.RedisSerializationContextBuilder<String, RouteDefinition> builder =
        RedisSerializationContext.newSerializationContext();
    return new ReactiveRedisTemplate<>(
        connectionFactory,
        builder
            .key(keySerializer)
            .value(valueSerializer)
            .hashKey(keySerializer)
            .hashValue(valueSerializer)
            .build());
  }
}
