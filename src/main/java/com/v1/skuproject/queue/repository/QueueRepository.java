package com.v1.skuproject.queue.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class QueueRepository {

    private final StringRedisTemplate redisTemplate;

    @Value("${queue.key}")
    private String queueKey;

    public void add(String value, double score) {
        redisTemplate.opsForZSet().add(queueKey, value, score);
    }

    public Long rank(String value) {
        return redisTemplate.opsForZSet().rank(queueKey, value);
    }

    public Long size() {
        return redisTemplate.opsForZSet().zCard(queueKey);
    }

    public Set<String> range(int start, int end) {
        return redisTemplate.opsForZSet().range(queueKey, start, end);
    }

    public void remove(String value) {
        redisTemplate.opsForZSet().remove(queueKey, value);
    }

    public void clear() {
        redisTemplate.delete(queueKey);
    }
}