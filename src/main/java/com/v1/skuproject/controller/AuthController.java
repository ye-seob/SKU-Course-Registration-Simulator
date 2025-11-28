package com.v1.skuproject.controller;

import com.v1.skuproject.dto.user.UserRequest.Login;
import com.v1.skuproject.dto.user.UserRequest.SignUp;
import com.v1.skuproject.dto.user.UserResponse.UserDto;
import com.v1.skuproject.service.UserService;
import com.v1.skuproject.util.response.ApiResponse;
import com.v1.skuproject.util.response.ResponseHandler;
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
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signUp(@Valid @RequestBody SignUp request) {
        log.info("Signup 컨트롤러 진입" + request.toString());
        Long userId = userService.createUser(request);

        log.debug("Signup 성공  userId: {}", userId);

        return ResponseHandler.ok(userId);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@Valid @RequestBody Login request) {
        log.info("login 컨트롤러 진입" + request.toString());
        UserDto response = userService.login(request);

        log.debug("Login 성공  userId: {}", response.toString());

        return ResponseHandler.ok(response);
    }
}