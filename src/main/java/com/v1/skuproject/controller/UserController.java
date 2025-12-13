package com.v1.skuproject.controller;

import com.v1.skuproject.dto.user.UserResponse.UserDto;
import com.v1.skuproject.service.UserService;
import com.v1.skuproject.util.response.ApiResponse;
import com.v1.skuproject.util.response.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<ApiResponse<UserDto>> getUser(Authentication authentication) {
        // 인증 객체에서 현재 로그인한 사용자의 id를 가져옴
        Long userId = (Long) authentication.getPrincipal();

        UserDto response = userService.getUserById(userId);

        return ResponseHandler.ok(response);
    }


    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<String>> deleteUser(Authentication authentication) {
        // 인증 객체에서 현재 로그인한 사용자의 id를 가져옴
        Long userId = (Long) authentication.getPrincipal();

        userService.deleteUserById(userId);

        return ResponseHandler.ok("회원 탈퇴 성공");
    }
}