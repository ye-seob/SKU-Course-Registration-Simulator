package com.v1.skuproject.controller;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.service.LectureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lectures")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    /**
     * 강의 조회 (전공, 타입, 이름 검색)
     */
    @GetMapping
    public ResponseEntity<List<Lecture>> getLectures(
            @RequestParam(name = "major", required = false) Major major,
            @RequestParam(name = "type", required = false) LectureType type,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        List<Lecture> lectures = lectureService.searchLectures(major, type, keyword);
        return ResponseEntity.ok(lectures); 
    }
}