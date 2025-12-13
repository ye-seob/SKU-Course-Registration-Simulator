package com.v1.skuproject.domain.enrollment;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "lecture_id"})})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 어떤 강의에
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    // 신청 상태 (SUCCESS, FAIL, PENDING)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    // 실패 사유
    @Column(length = 255)
    private String reason;

    // 대기 시간
    @Column(name = "wait_time")
    private Long waitTime;

    // 생성 시간
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Enrollment success(User user, Lecture lecture) {
        return Enrollment.builder()
                .user(user)
                .lecture(lecture)
                .status(EnrollmentStatus.SUCCESS)
                .build();
    }

    public static Enrollment fail(User user, Lecture lecture, String reason) {
        return Enrollment.builder()
                .user(user)
                .lecture(lecture)
                .status(EnrollmentStatus.FAIL)
                .reason(reason)
                .build();
    }
}