package com.v1.skuproject.domain.lecture;

import com.v1.skuproject.domain.user.Major;
import jakarta.persistence.*;
import lombok.*;

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
    private int credit;

    // 정원
    @Column(nullable = false)
    private int capacity;

    // 현재 수강신청한 인원
    @Column(nullable = false)
    private int enrollment;

    // 평점
    @Column(nullable = false)
    private double rating;

    // 단순 문자열, 필요 시 JSON 또는 별도 테이블로 확장 가능
    @Column(nullable = false)
    private String schedule;

    // 어떤 학과의 전공인지, 교양이라면 null
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Major major;
}