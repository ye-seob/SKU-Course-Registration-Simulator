package com.v1.skuproject.util;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestResetController {

    private final EntityManager em;
    private final StringRedisTemplate redisTemplate;

    @Value("${queue.key}")
    private String queueKey;

    @DeleteMapping("/reset")
    @Transactional
    public ResponseEntity<Void> reset() {

        long lectureId = 1L;

        // enrollment 삭제
        em.createQuery("""
            DELETE FROM Enrollment e
            WHERE e.lecture.id = :lectureId
        """)
                .setParameter("lectureId", lectureId)
                .executeUpdate();

        // lecture enrollment = 0
        em.createQuery("""
            UPDATE Lecture l
            SET l.enrollment = 0
            WHERE l.id = :lectureId
        """)
                .setParameter("lectureId", lectureId)
                .executeUpdate();

        // Redis queue 초기화
        redisTemplate.delete(queueKey);

        return ResponseEntity.noContent().build();
    }
}