package com.v1.skuproject.user.controller;

import com.v1.skuproject.auth.dto.UserResponse.UserDto;
import com.v1.skuproject.common.response.ApiResponse;
import com.v1.skuproject.common.response.ResponseHandler;
import com.v1.skuproject.config.security.UserPrincipal;
import com.v1.skuproject.user.dto.UserCountResponse;
import com.v1.skuproject.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 정보 조회 및 회원 탈퇴 API")
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getUser(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        log.info("내 정보 조회 요청 userId={}", userId);

        UserDto response = userService.getUserById(userId);

        return ResponseHandler.ok(response);
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        log.info("회원 탈퇴 요청 userId={}", userId);

        userService.deleteUserById(userId);

        log.info("회원 탈퇴 성공 userId={}", userId);

        return ResponseHandler.ok("회원 탈퇴 성공");
    }

    @Operation(summary = "총 가입자 수 조회")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<UserCountResponse>> getUserCount() {

        long count = userService.getUserCount();

        UserCountResponse response = new UserCountResponse(count);

        return ResponseHandler.ok(response);
    }
}