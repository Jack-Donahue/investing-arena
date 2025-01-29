package com.jackdonahue.investingarena.Service;

import com.jackdonahue.investingarena.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    //Prefix for redis keys for the user. "ex - user:100"
    private static final String USER_KEY_PREFIX = "user:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //Saves a user object in Redis
    public void saveUser(String username, User user) {
        if (username == null || username.isEmpty() || user == null) {
            throw new IllegalArgumentException("Username and user data must not be null or empty");
        }
        redisTemplate.opsForValue().set(username, user);
    }

}
