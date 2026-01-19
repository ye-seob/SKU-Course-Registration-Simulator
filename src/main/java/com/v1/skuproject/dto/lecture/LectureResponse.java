package com.v1.skuproject.dto.lecture;


import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LectureResponse {
    private final Double credit;
    private Long lectureId;
    private String lectureName;
    private int enrollment;
    private Integer capacity;
    private Major major;
    private int classNumber;
    private double time;
    private String professor;
    private String schedule;
    private double rating;
    private String room;
    private String gradingMethod;
    private String lectureCode;
    private LectureType type;



    public static LectureResponse from(Lecture lecture) {
        return LectureResponse.builder()
                .lectureId(lecture.getId())
                .lectureName(lecture.getLectureName())
                .enrollment(lecture.getEnrollment())
                .capacity(lecture.getCapacity())
                .major(lecture.getMajor())
                .credit(lecture.getCredit())
                .classNumber(lecture.getClassNumber())
                .time(lecture.getTime())
                .professor(lecture.getProfessor())
                .schedule(lecture.getSchedule())
                .rating(lecture.getRating())
                .room(lecture.getRoom())
                .gradingMethod(lecture.getGradingMethod())
                .lectureCode(lecture.getLectureCode())
                .type(lecture.getType())
                .build();
    }

}