package com.v1.skuproject.controller;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.service.LectureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/lectures")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping
    public ResponseEntity<List<Lecture>> getLectures(
            @RequestParam(name = "major", required = false) Major major,
            @RequestParam(name = "type", required = false) LectureType type,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        log.info("getLectures 컨트롤러 진입  major: {}, type: {}, keyword: {}", major, type, keyword);
        List<Lecture> lectures = lectureService.searchLectures(major, type, keyword);
        log.info("getLectures 성공  조회된 강의 수: {}", lectures.size());
        return ResponseEntity.ok(lectures); 
    }
}