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
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> list(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<EnrollmentResponse> response = enrollmentService.getEnrollments(userId);
        return ResponseHandler.ok(response);
    }

    @Operation(summary = "수강 신청", description = "강의 신청")
    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(
            Authentication authentication,
            @RequestParam(name = "lectureId") Long lectureId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        EnrollmentResponse response = enrollmentService.enroll(userId, lectureId);


        return ResponseHandler.ok(response);
    }

    @Operation(summary = "수강 신청 취소", description = "강의 신청 취소")
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> cancel(
            Authentication authentication,
            @RequestParam(name = "lectureId") Long lectureId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        EnrollmentResponse response = enrollmentService.cancelEnrollment(userId, lectureId);
        return ResponseHandler.ok(response);
    }
}