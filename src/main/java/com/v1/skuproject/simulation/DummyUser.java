package com.v1.skuproject.simulation;

import lombok.Builder;
import lombok.Getter;

/**
 * 시뮬레이션용 더미 유저 객체 (메모리 전용)
 */
@Getter
@Builder
public class DummyUser {

    private Long id;        // (100000번대 사용)
    private String name;
    private Long targetLectureId;  // 신청할 강의 ID


    public static DummyUser create(Long id, Long lectureId) {
        return DummyUser.builder()
                .id(100000L + id)
                .name("Dummy_" + id)
                .targetLectureId(lectureId)
                .build();
    }
}