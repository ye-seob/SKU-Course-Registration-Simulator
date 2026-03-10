package com.v1.skuproject.queue.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QueueSubscriberService {
    private final Map<Long, Long> subscribers = new ConcurrentHashMap<>();

    public void subscribe(Long userId, Long lectureId) {
        subscribers.put(userId, lectureId);
    }

    public void unsubscribe(Long userId) {
        subscribers.remove(userId);
    }

    public Long getLecture(Long userId) {
        return subscribers.get(userId);
    }

    public Map<Long, Long> getSubscribers() {
        return subscribers;
    }

    public void clear() {
        subscribers.clear();
    }
}