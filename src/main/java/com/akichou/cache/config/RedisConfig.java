package com.akichou.cache.config;

import com.akichou.cache.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>() ;

        Jackson2JsonRedisSerializer<User> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(User.class) ;

        redisTemplate.setConnectionFactory(connectionFactory) ;
        redisTemplate.setKeySerializer(new StringRedisSerializer()) ;
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer) ;

        return redisTemplate ;
    }
}
