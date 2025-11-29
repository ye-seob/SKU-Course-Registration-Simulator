package com.v1.skuproject.controller;

import com.v1.skuproject.service.EnrollmentService;
import com.v1.skuproject.util.response.ApiResponse;
import com.v1.skuproject.util.response.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/enrollments")
@Tag(name = "Enrollment", description = "강의 신청 관련 API")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "수강 신청", description = "강의 신청")
    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<String>> enroll(
            Authentication authentication,
            @RequestParam(name = "lectureId") Long lectureId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("컨트롤러 진입 - userId: {}, lectureId: {}", userId, lectureId);

        boolean success = enrollmentService.enroll(userId, lectureId);

        if (success) {
            log.info("수강 신청 성공 - userId: {}, lectureId: {}", userId, lectureId);
            return ResponseHandler.ok("수강 신청 성공");
        } else {
            log.warn("수강 신청 실패 - userId: {}, lectureId: {} (중복 또는 정원 초과)", userId, lectureId);
            return ResponseHandler.error("123", "수강 신청 실패 (중복 또는 정원 초과)", HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "수강 취소", description = "강의 신청 취소")
    @DeleteMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> cancel(
            Authentication authentication,
            @RequestParam(name = "lectureId") Long lectureId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("컨트롤러 진입 - userId: {}, lectureId: {}", userId, lectureId);

        boolean success = enrollmentService.cancel(userId, lectureId);

        if (success) {
            log.info("수강 취소 성공 - userId: {}, lectureId: {}", userId, lectureId);
            return ResponseHandler.ok("수강 취소 성공");
        } else {
            log.warn("수강 취소 실패 - userId: {}, lectureId: {} (신청 내역 없음)", userId, lectureId);
            return ResponseHandler.error("123", "수강 취소 실패 (신청 내역 없음)", HttpStatus.CONFLICT);
        }
    }
}