package com.v1.skuproject.controller;

import com.v1.skuproject.dto.queue.QueueRankResponse;
import com.v1.skuproject.service.QueueService;
import com.v1.skuproject.util.response.ApiResponse;
import com.v1.skuproject.util.response.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/queue")
@Tag(name = "Queue", description = "수강신청 대기열 관련 API")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;


    @PostMapping("/enter")
    @Operation(
            summary = "대기열 입장",
            description = "특정 강의에 대해 수강신청 대기열에 사용자를 등록"
    )

    public ResponseEntity<ApiResponse<String>> enterQueue(
            Authentication authentication,
            @RequestParam(name = "lectureId") Long lectureId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        queueService.enter(userId, lectureId);

        return ResponseHandler.ok("대기열에 등록되었습니다.");
    }

    @GetMapping("/rank")
    @Operation(
            summary = "대기열 순번 조회",
            description = "사용자가 대기열에서  몇 번째 순번인지 조회"
    )

    public ResponseEntity<ApiResponse<QueueRankResponse>> getRank(
            Authentication authentication,
            @RequestParam(name = "lectureId") Long lectureId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        QueueRankResponse rankInfo = queueService.getRank(userId, lectureId);

        return ResponseHandler.ok(rankInfo);
    }


    @PostMapping("/exit")
    @Operation(
            summary = "대기열 퇴장",
            description = "사용자를 수강신청 대기열에서 제거"
    )

    public ResponseEntity<ApiResponse<String>> exitQueue(
            Authentication authentication,
            @RequestParam(name = "lectureId") Long lectureId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        queueService.exit(userId, lectureId);

        return ResponseHandler.ok("대기열에서 제외되었습니다.");
    }
}
