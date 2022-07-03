package com.lzhpo.panda.gateway;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author lzhpo
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
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
  public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
    return new StringRedisTemplate(connectionFactory);
  }
}
