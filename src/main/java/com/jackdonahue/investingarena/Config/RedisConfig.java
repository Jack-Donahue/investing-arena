package com.jackdonahue.investingarena.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//Configures a template to interact with Redis
//Uses connection factory to set up connection to redis
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        //Use a String serializer for the keys
        template.setKeySerializer(new StringRedisSerializer());

        //Use JSON serializer for the values
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}