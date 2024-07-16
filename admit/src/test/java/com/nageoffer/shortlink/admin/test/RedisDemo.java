package com.nageoffer.shortlink.admin.test;

import io.lettuce.core.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisDemo {
    public static void main(String[] args) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
    }
}
