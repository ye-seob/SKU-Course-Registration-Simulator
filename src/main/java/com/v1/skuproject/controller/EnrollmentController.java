package com.v1.skuproject.controller;

import com.v1.skuproject.dto.enrollment.EnrollmentResponse;
import com.v1.skuproject.service.EnrollmentService;
import com.v1.skuproject.util.response.ApiResponse;
import com.v1.skuproject.util.response.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/v1/enrollments")
@Tag(name = "Enrollment", description = "강의 신청 관련 API")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "수강 신청", description = "강의 신청")
    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(
            Authentication authentication,
            @RequestParam(name = "lectureId") Long lectureId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("수강 신청 요청 - userId: {}, lectureId: {}", userId, lectureId);

        EnrollmentResponse response = enrollmentService.enroll(userId, lectureId);

        log.info("수강 신청 결과 - status: {}, reason: {}", response.getStatus(), response.getReason());

        return ResponseHandler.ok(response);
    }
}