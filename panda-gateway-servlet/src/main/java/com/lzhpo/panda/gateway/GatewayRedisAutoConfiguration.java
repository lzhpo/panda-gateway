package com.lzhpo.panda.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.lzhpo.panda.gateway.core.route.RouteDefinition;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author lzhpo
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({RedisScript.class, StringRedisTemplate.class})
@ConditionalOnProperty(prefix = "gateway.redis", value = "enabled", havingValue = "true")
public class GatewayRedisAutoConfiguration {

  private final Jackson2ObjectMapperBuilder objectMapperBuilder;

  @Bean
  public RedisTemplate<String, RouteDefinition> redisTemplate(
      RedisConnectionFactory connectionFactory) {
    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    RedisTemplate<String, RouteDefinition> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setKeySerializer(stringRedisSerializer);
    redisTemplate.setValueSerializer(serializer());
    redisTemplate.setHashKeySerializer(stringRedisSerializer);
    redisTemplate.setHashValueSerializer(serializer());
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }

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
  public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
    return new StringRedisTemplate(connectionFactory);
  }

  private RedisSerializer<Object> serializer() {
    Jackson2JsonRedisSerializer<Object> redisSerializer =
        new Jackson2JsonRedisSerializer<>(Object.class);
    ObjectMapper om = objectMapperBuilder.build();
    // If not set it, we get redis data just is json, will not have java type.
    om.activateDefaultTyping(om.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL);
    redisSerializer.setObjectMapper(om);
    return redisSerializer;
  }
}
