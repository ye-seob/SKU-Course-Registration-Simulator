package com.v1.skuproject.controller;

import com.v1.skuproject.dto.user.UserRequest.Login;
import com.v1.skuproject.dto.user.UserRequest.SignUp;
import com.v1.skuproject.dto.user.UserResponse.UserDto;
import com.v1.skuproject.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 추후 로그인 회원가입 AuthController로 분리
//  jwt 추가
// 권한 검증 추가하기
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
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

    @GetMapping("/id/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable("userId") Long userId) {
        UserDto response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/id/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}
