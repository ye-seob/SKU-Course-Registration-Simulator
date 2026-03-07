package com.v1.skuproject.controller;

import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import com.v1.skuproject.common.response.ApiResponse;
import com.v1.skuproject.common.response.ResponseHandler;
import com.v1.skuproject.config.security.UserPrincipal;
import com.v1.skuproject.domain.cart.CartLecture;
import com.v1.skuproject.dto.lecture.CartLectureResponse;
import com.v1.skuproject.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Cart", description = "장바구니 관련 API")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 담기")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartLectureResponse>> addLectureToCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, Long> body
    ) {

        Long userId = principal.getUserId();
        Long lectureId = body.get("lectureId");

        if (lectureId == null) {
            throw new BaseException(ErrorCode.PARAMETER_MISSING);
        }

        log.info("장바구니 담기 요청 userId={} lectureId={}", userId, lectureId);

        CartLecture cartLecture = cartService.addLectureToCart(userId, lectureId);

        log.info("장바구니 담기 성공 userId={} lectureId={}", userId, lectureId);

        return ResponseHandler.ok(CartLectureResponse.from(cartLecture));
    }

    @Operation(summary = "장바구니 취소")
    @DeleteMapping("/delete/{lectureId}")
    public ResponseEntity<ApiResponse<String>> removeLectureFromCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long lectureId
    ) {
        Long userId = principal.getUserId();

        log.info("장바구니 삭제 요청 userId={} lectureId={}", userId, lectureId);

        cartService.removeLectureFromCart(userId, lectureId);

        log.info("장바구니 삭제 성공 userId={} lectureId={}", userId, lectureId);

        return ResponseHandler.ok("장바구니 강의 삭제 성공");
    }

    @Operation(summary = "장바구니 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CartLectureResponse>>> getCartLectures(
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long userId = principal.getUserId();

        log.info("장바구니 조회 요청 userId={}", userId);

        List<CartLecture> cartLectures = cartService.getCartLectures(userId);

        return ResponseHandler.ok(CartLectureResponse.from(cartLectures));
    }
}