package com.v1.skuproject.enrollment.dto;

import com.v1.skuproject.enrollment.entity.Enrollment;
import com.v1.skuproject.lecture.entity.Lecture;
import com.v1.skuproject.lecture.entity.LectureType;
import com.v1.skuproject.user.entity.Major;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EnrollmentResponse {
    private LocalDateTime createdAt; // 신청 시각

    // 강의 정보
    private Long lectureId;
    private String lectureName;
    private String professor;
    private String lectureCode;
    private Integer classNumber;
    private LectureType type;
    private Double credit;
    private Double time;
    private String schedule;
    private Major major;
    private Integer enrollment; // 현재 수강 인원
    private Integer capacity;   // 총 정원
    private Double rating;      // 강의 평점
    private String gradingMethod; // 강의 평가 방법
    private String room;          // 강의실



    public static EnrollmentResponse from(Enrollment e) {
        Lecture l = e.getLecture();

        return EnrollmentResponse.builder()
                .createdAt(e.getCreatedAt())
                .lectureId(l.getId())
                .lectureName(l.getLectureName())
                .professor(l.getProfessor())
                .lectureCode(l.getLectureCode())
                .classNumber(l.getClassNumber())
                .type(l.getType())
                .credit(l.getCredit())
                .time(l.getTime())
                .schedule(l.getSchedule())
                .major(l.getMajor())
                .enrollment(l.getEnrollment())
                .capacity(l.getCapacity())
                .rating(l.getRating())
                .gradingMethod(l.getGradingMethod())
                .room(l.getRoom())
                .build();
    }


}