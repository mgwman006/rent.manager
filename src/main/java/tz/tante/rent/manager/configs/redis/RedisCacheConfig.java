package tz.tante.rent.manager.configs.redis;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import java.time.Duration;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisCacheConfig {

  @Bean
  public RedisCacheConfiguration redisCacheConfiguration() {

    GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer()
        .configure(mapper -> {
          mapper.registerModule(new JavaTimeModule());
          mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        });

    return RedisCacheConfiguration.defaultCacheConfig()
      .entryTtl(Duration.ofMinutes(5))
      .serializeValuesWith(
        RedisSerializationContext
          .SerializationPair
          .fromSerializer(serializer)
      );
  }
}

