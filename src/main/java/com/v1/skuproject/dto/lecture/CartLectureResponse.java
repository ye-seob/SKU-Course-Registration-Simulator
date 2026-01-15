package com.v1.skuproject.dto.lecture;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.v1.skuproject.domain.cart.CartLecture;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Builder
public class CartLectureResponse {


    private final Long cartId;

    private final Long lectureId;


    private final String lectureName;

    // 교수명
    private final String professor;

    // 학수 번호 (예: SW0101)
    private final String lectureCode;

    // 분반 정보 (예: 1)
    private final Integer classNumber;

    // 이수 구분 (예: MAJOR_CORE, 교양 등)
    private final LectureType type;

    // 학점
    private final Double credit;

    private final Double time;

    // 강의 시간표 정보 (예: 월(9:00~11:45))
    private final String schedule;

    // 개설 전공
    private final Major major;

    // 현재 수강 인원
    private final Integer enrollment;

    // 총 수강 정원
    private final Integer capacity;

    // 강의 평점
    private final Double rating;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime addedAt;


    public static CartLectureResponse from(CartLecture cartLecture) {

        return CartLectureResponse.builder()
                .cartId(cartLecture.getId().getCartId())
                .lectureId(cartLecture.getId().getLectureId())
                .lectureName(cartLecture.getLecture().getLectureName())
                .professor(cartLecture.getLecture().getProfessor())
                .lectureCode(cartLecture.getLecture().getLectureCode())
                .classNumber(cartLecture.getLecture().getClassNumber())
                .type(cartLecture.getLecture().getType())
                .credit(cartLecture.getLecture().getCredit())
                .time(cartLecture.getLecture().getTime())
                .schedule(cartLecture.getLecture().getSchedule())
                .major(cartLecture.getLecture().getMajor())
                .enrollment(cartLecture.getLecture().getEnrollment())
                .capacity(cartLecture.getLecture().getCapacity())
                .rating(cartLecture.getLecture().getRating())


                .addedAt(cartLecture.getAddedAt())
                .build();
    }

    public static List<CartLectureResponse> from(List<CartLecture> cartLectures) {
        // stream()과 map()을 사용하여 각 엔티티를 DTO로 변환
        return cartLectures.stream()
                .map(CartLectureResponse::from)
                .collect(Collectors.toList());
    }
}

