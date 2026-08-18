package com.v1.skuproject.enrollment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrollmentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long lectureId;

    private LocalDateTime attemptedAt;

    @Enumerated(EnumType.STRING)
    private AttemptResult result;

    public static EnrollmentAttempt of(Long userId, Long lectureId, AttemptResult result) {
        EnrollmentAttempt attempt = new EnrollmentAttempt();
        attempt.userId = userId;
        attempt.lectureId = lectureId;
        attempt.result = result;
        attempt.attemptedAt = LocalDateTime.now();
        return attempt;
    }
}