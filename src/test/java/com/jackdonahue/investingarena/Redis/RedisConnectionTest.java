package com.jackdonahue.investingarena.Redis;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import static org.junit.jupiter.api.Assertions.*;


class RedisConnectionTest {
    //Jedis - Java for Redis
    @Test
    void testRedisConnection() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String pong = jedis.ping();
            assertEquals("PONG", pong);
        }
    }
}
