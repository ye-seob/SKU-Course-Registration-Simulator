package com.v1.skuproject.auth.controller;

import com.v1.skuproject.auth.dto.UserRequest.GuestLoginRequest;
import com.v1.skuproject.auth.dto.UserRequest.Login;
import com.v1.skuproject.auth.dto.UserRequest.SignUp;
import com.v1.skuproject.auth.dto.UserResponse.UserDto;
import com.v1.skuproject.auth.service.AuthService;
import com.v1.skuproject.common.response.ApiResponse;
import com.v1.skuproject.common.response.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원가입 및 로그인 관련 API")
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signUp(@Valid @RequestBody SignUp request) {

        Long userId = authService.createUser(request);

        return ResponseHandler.ok(userId);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@Valid @RequestBody Login request) {

        UserDto response = authService.login(request);

        return ResponseHandler.ok(response);
    }

    @Operation(summary = "게스트 로그인")
    @PostMapping("/guest")
    public ResponseEntity<ApiResponse<UserDto>> guestLogin(@Valid @RequestBody GuestLoginRequest request) {

        UserDto response = authService.guestLogin(request);

        return ResponseHandler.ok(response);
    }
}