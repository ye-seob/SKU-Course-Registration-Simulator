package com.v1.skuproject.controller;

import com.v1.skuproject.domain.cart.CartLecture;
import com.v1.skuproject.dto.lecture.CartLectureResponse;
import com.v1.skuproject.service.CartService;
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
        Authentication authentication,
        @RequestBody Map<String, Long> body
    ) {
        Long lectureId = body.get("lectureId");
        Long userId = (Long) authentication.getPrincipal();

        CartLecture cartLecture = cartService.addLectureToCart(userId, lectureId);


        return ResponseHandler.ok(CartLectureResponse.from(cartLecture));
    }

	@Operation(summary = "장바구니 취소")
	@DeleteMapping("/delete/{lectureId}")
	public ResponseEntity<ApiResponse<String>> removeLectureFromCart(
       		Authentication authentication,
      		@PathVariable Long lectureId
	)
      {  Long userId = (Long) authentication.getPrincipal();

        cartService.removeLectureFromCart(userId, lectureId);

        return ResponseHandler.ok("장바구니 강의 삭제 성공");
    }


    @Operation(summary = "장바구니 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CartLectureResponse>>> getCartLectures(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();


        List<CartLecture> cartLectures = cartService.getCartLectures(userId);


        return ResponseHandler.ok(CartLectureResponse.from(cartLectures));
    }
}
