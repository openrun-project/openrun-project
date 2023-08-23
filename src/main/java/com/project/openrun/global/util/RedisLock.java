package com.project.openrun.global.util;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisLock {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean tryLock(String key, long timeout) {
        ValueOperations<String, String> opsValue = redisTemplate.opsForValue();
        Boolean lock = opsValue.setIfAbsent(key, "LOCK", timeout, TimeUnit.SECONDS);
        return lock != null ? lock : false;
    }

    public void unlock(String key) {
        redisTemplate.delete(key);
    }
}
