package com.v1.skuproject.controller;

import com.v1.skuproject.common.response.ApiResponse;
import com.v1.skuproject.common.response.ResponseHandler;
import com.v1.skuproject.config.security.UserPrincipal;
import com.v1.skuproject.dto.enrollment.EnrollmentResponse;
import com.v1.skuproject.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/enrollments")
@Tag(name = "Enrollment", description = "강의 신청 관련 API")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "수강 신청 조회", description = "사용자가 신청한 강의 내역 조회")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> list(
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long userId = principal.getUserId();
        log.info("수강 신청 목록 조회 요청 userId={}", userId);

        List<EnrollmentResponse> response = enrollmentService.getEnrollments(userId);

        return ResponseHandler.ok(response);
    }

    @Operation(summary = "수강 신청 취소", description = "강의 신청 취소")
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> cancel(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(name = "lectureId") Long lectureId
    ) {

        Long userId = principal.getUserId();
        log.info("수강 신청 취소 요청 userId={} lectureId={}", userId, lectureId);

        EnrollmentResponse response = enrollmentService.cancelEnrollment(userId, lectureId);

        log.info("수강 신청 취소 성공 userId={} lectureId={}", userId, lectureId);

        return ResponseHandler.ok(response);
    }
}