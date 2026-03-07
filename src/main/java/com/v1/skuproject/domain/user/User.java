package com.v1.skuproject.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId;

    @Column(nullable = false)
    private String name;

    //학과
    @Column(unique = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private  Major major;

    // 학년
    @Column(nullable = false)
    private int grade;

    // 최대 신청 가능 학점
    @Column(name = "max_credit" ,nullable = false)
    private int maxCredit;

    // 최소 신청 가능 학점
    @Column(name = "min_credit" ,nullable = false)
    private int minCredit;

    @Column(name = "role" , nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
}
