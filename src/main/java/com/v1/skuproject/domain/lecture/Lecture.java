package com.v1.skuproject.domain.lecture;

import com.v1.skuproject.domain.user.Major;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lectures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String professor;

    // 강의명
    @Column(name = "lecture_name", nullable = false)
    private String lectureName;

    // 학수 번호
    @Column(name = "lecture_code", nullable = false)
    private String lectureCode;

    // 분반
    @Column(name = "class_number", nullable = false)
    private int classNumber;

    // 전핵 전선 교선 교핵
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LectureType type;

    // 학점
    @Column(nullable = false)
    private double credit;

    // 몇시간 강의인지
    @Column(nullable = false)
    private double time;

    // 정원
    @Column(nullable = false)
    private Integer capacity;

    // 현재 수강신청한 인원
    @Column(nullable = false)
    private Integer enrollment;

    // 강의 평점
    @Column(nullable = false)
    private double rating;

    // 평점 개수
    @Column(name = "rating_count", nullable = false)
    private int ratingCount;

    @Column(name = "grading_method",nullable = true)
    private String gradingMethod;

    // 강의실
    @Column(name = "room")
    private String room;

    // {"월":["25","26"],"수":["21","22"],"목":["22","22"]}
    @Column(nullable = false, columnDefinition = "TEXT")
    private String schedule;

    // 어떤 학과의 전공인지, 교양이라면 null
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Major major;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.PERSIST)
    private List<LectureRating> ratings = new ArrayList<>();


    public void incrementEnrollment() {
        if (this.enrollment < this.capacity) {
            this.enrollment += 1;
        }
    }

    public void decrementEnrollment() {
        if (this.enrollment > 0) {
            this.enrollment -= 1;
        }
    }

    public void resetEnrolledCount() {
        this.enrollment = 0;
    }


    public LectureRating findRatingByUserId(Long userId) {
        return ratings.stream()
                .filter(r -> r.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    // 평점 추가
    public void addRating(int score) {
        double totalScore = this.rating * this.ratingCount;
        this.ratingCount += 1;
        this.rating = (totalScore + score) / this.ratingCount;
    }

    // 기존 평점 수정
    public void updateRating(int oldScore, int newScore) {
        double totalScore = this.rating * this.ratingCount;
        this.rating = (totalScore - oldScore + newScore) / this.ratingCount;
    }


}