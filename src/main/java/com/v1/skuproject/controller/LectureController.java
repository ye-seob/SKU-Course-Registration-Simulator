package com.v1.skuproject.controller;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.service.LectureService;
import com.v1.skuproject.util.response.ApiResponse;
import com.v1.skuproject.util.response.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ApiResponse<List<Lecture>>> getLectures(
            @RequestParam(name = "major", required = false) Major major,
            @RequestParam(name = "type", required = false) LectureType type,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {

        List<Lecture> lectures = lectureService.searchLectures(major, type, keyword);


        return ResponseHandler.ok(lectures);
    }
}