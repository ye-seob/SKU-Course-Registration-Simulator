package com.v1.skuproject.controller;

import com.v1.skuproject.common.response.ApiResponse;
import com.v1.skuproject.common.response.ResponseHandler;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.dto.lecture.LectureResponse;
import com.v1.skuproject.service.LectureService;
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
@RequiredArgsConstructor
@Tag(name = "Lecture", description = "강의 조회 관련 API")
@RequestMapping("/api/v1/lectures")
public class LectureController {

    private final LectureService lectureService;

    @Operation(
            summary = "강의 조회",
            description = "어느 학과인지, 강의 유형, 검색어를 기준으로 강의를 검색. 모든 파라미터는 선택 사항"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<LectureResponse>>> getLectures(
            @RequestParam(name = "major", required = false) Major major,
            @RequestParam(name = "type", required = false) LectureType type,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        log.info(
                "강의 조회 요청 major={} type={} keyword={}",
                major, type, keyword
        );

        List<LectureResponse> lectures = lectureService.searchLectures(major, type, keyword);

        return ResponseHandler.ok(lectures);
    }

    @Operation(
            summary = "강의 평점 등록 / 수정",
            description = "사용자가 강의에 평점을 등록하거나 수정합니다."
    )
    @PostMapping("/{lectureId}/rating")
    public ResponseEntity<ApiResponse<String>> rateLecture(
            Authentication authentication,
            @PathVariable(name = "lectureId") Long lectureId,
            @RequestParam(name = "score") int score
    ) {
        Long userId = (Long) authentication.getPrincipal();

        log.info(
                "강의 평점 등록 요청 userId={} lectureId={} score={}",
                userId, lectureId, score
        );

        lectureService.rateLecture(userId, lectureId, score);

        log.info(
                "강의 평점 등록 성공 userId={} lectureId={}",
                userId, lectureId
        );

        return ResponseHandler.ok("강의 평점이 등록되었습니다.");
    }
}