package com.v1.skuproject.queue.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueueEntry {

    private final Long userId;
    private final Long lectureId;

    public static QueueEntry of(Long userId, Long lectureId) {
        return new QueueEntry(userId, lectureId);
    }

    public static QueueEntry decode(String value) {
        String[] parts = value.split(":");
        return new QueueEntry(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
    }

    public String encode() {
        return userId + ":" + lectureId;
    }

    public boolean isDummy() {
        return userId >= 100_000L;
    }
}