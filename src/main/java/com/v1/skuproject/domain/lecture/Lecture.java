package com.v1.skuproject.domain.lecture;

import com.v1.skuproject.domain.user.Professor;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lecture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 교수와 N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

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
    private int credit;

    // 정원
    @Column(nullable = false)
    private int capacity;

    // 평점
    @Column(nullable = false)
    private double rating;

    // 단순 문자열, 필요 시 JSON 또는 별도 테이블로 확장 가능
    @Column(nullable = false)
    private String schedule;
}