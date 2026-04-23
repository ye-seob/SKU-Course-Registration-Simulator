package com.v1.skuproject.practice.entity;

import com.v1.skuproject.lecture.entity.Lecture;
import com.v1.skuproject.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "practice_enrollments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PracticeEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static PracticeEnrollment of(User user, Lecture lecture) {
        return PracticeEnrollment.builder()
                .user(user)
                .lecture(lecture)
                .createdAt(LocalDateTime.now())
                .build();
    }
}