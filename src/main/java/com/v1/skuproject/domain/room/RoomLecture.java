package com.v1.skuproject.domain.room;

import com.v1.skuproject.domain.lecture.Lecture;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_lecture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RoomLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Room과 N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // Lecture와 N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    // 현재 수강 신청한 인원
    @Column(name = "current_enrolled", nullable = false)
    private int currentEnrolled;

    // 정원
    @Column(name = "capacity", nullable = false)
    private int capacity;

    // 수강 신청 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    // 담긴 시간
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;



    public enum Status {
        PENDING,
        SUCCESS,
        FAIL
    }
}