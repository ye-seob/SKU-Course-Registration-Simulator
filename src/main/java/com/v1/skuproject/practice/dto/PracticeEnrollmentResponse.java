package com.v1.skuproject.practice.dto;

import com.v1.skuproject.lecture.entity.Lecture;
import com.v1.skuproject.lecture.entity.LectureType;
import com.v1.skuproject.practice.entity.PracticeEnrollment;
import com.v1.skuproject.user.entity.Major;
import lombok.Getter;

@Getter
public class PracticeEnrollmentResponse {

    private Long id;

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

    public static PracticeEnrollmentResponse from(PracticeEnrollment entity) {

        Lecture lecture = entity.getLecture();

        PracticeEnrollmentResponse response = new PracticeEnrollmentResponse();
        response.id = entity.getId();

        response.lectureId = lecture.getId();
        response.lectureName = lecture.getLectureName();
        response.professor = lecture.getProfessor();
        response.lectureCode = lecture.getLectureCode();
        response.classNumber = lecture.getClassNumber();
        response.type = lecture.getType();
        response.credit = lecture.getCredit();
        response.time = lecture.getTime();
        response.schedule = lecture.getSchedule();
        response.major = lecture.getMajor();
        response.enrollment = lecture.getEnrollment();
        response.capacity = lecture.getCapacity();
        response.rating = lecture.getRating();
        response.gradingMethod = lecture.getGradingMethod();
        response.room = lecture.getRoom();

        return response;
    }
}