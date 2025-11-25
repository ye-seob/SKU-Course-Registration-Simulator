package com.v1.skuproject.controller;

import com.v1.skuproject.dto.user.UserRequest.Login;
import com.v1.skuproject.dto.user.UserRequest.SignUp;
import com.v1.skuproject.dto.user.UserResponse.UserDto;
import com.v1.skuproject.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(@Valid @RequestBody SignUp request) {
        Long userId = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody Login request) {
        UserDto response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}