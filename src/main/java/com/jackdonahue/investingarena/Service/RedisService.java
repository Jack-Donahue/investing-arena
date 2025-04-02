package com.jackdonahue.investingarena.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveDataToRedis(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object getDataFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}