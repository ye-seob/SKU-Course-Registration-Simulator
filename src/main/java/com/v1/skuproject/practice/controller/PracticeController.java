package com.v1.skuproject.practice.controller;

import com.v1.skuproject.common.response.ApiResponse;
import com.v1.skuproject.common.response.ResponseHandler;
import com.v1.skuproject.config.security.UserPrincipal;
import com.v1.skuproject.practice.dto.PracticeEnrollmentResponse;
import com.v1.skuproject.practice.service.PracticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/practice")
@Tag(name = "Practice", description = "연습 모드 수강신청 API")
public class PracticeController {

    private final PracticeService practiceService;

    @Operation(summary = "연습 수강신청 목록 조회")
    @GetMapping("/enrollments")
    public ResponseEntity<ApiResponse<List<PracticeEnrollmentResponse>>> getPracticeEnrollments(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseHandler.ok(
                practiceService.getPracticeEnrollments(user.getUserId())
        );
    }

    @Operation(summary = "연습 수강신청")
    @PostMapping("/enroll/{lectureId}")
    public ResponseEntity<ApiResponse<String>> enroll(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable("lectureId") Long lectureId
    ) {
        practiceService.practiceEnroll(user.getUserId(), lectureId);
        return ResponseHandler.ok("수강신청 성공");
    }

    @Operation(summary = "연습 수강신청 취소")
    @DeleteMapping("/enroll/{lectureId}")
    public ResponseEntity<ApiResponse<String>> cancel(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable("lectureId") Long lectureId
    ) {
        practiceService.practiceCancel(user.getUserId(), lectureId);
        return ResponseHandler.ok("수강신청 취소 성공");
    }
}