package com.v1.skuproject.dto.enrollment;

import com.v1.skuproject.domain.enrollment.Enrollment;
import com.v1.skuproject.domain.enrollment.EnrollmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EnrollmentResponse {

    private EnrollmentStatus status; // SUCCESS, FAIL, PENDING
    private String reason;           // 실패 사유
    private LocalDateTime createdAt; // 신청 시각

    public static EnrollmentResponse from(Enrollment e) {
        return EnrollmentResponse.builder()
                .status(e.getStatus())
                .reason(e.getReason())
                .createdAt(e.getCreatedAt())
                .build();
    }

}