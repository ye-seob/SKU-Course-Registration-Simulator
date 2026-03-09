package com.v1.skuproject.lecture.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "lecture_ratings",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"lecture_id", "user_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 1 ~ 5
    @Column(nullable = false)
    private int score;

    public LectureRating(Lecture lecture, Long userId, int score) {
        this.lecture = lecture;
        this.userId = userId;
        this.score = score;
    }




    public void updateScore(int score) {
        this.score = score;
    }
}