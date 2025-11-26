package com.v1.skuproject.controller;

import com.v1.skuproject.dto.user.UserResponse.UserDto;
import com.v1.skuproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     유저 정보 조회
     jwt  필요
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        // 인증 객체에서 현재 로그인한 사용자의 id를 가져옴
        Long userId = (Long) authentication.getPrincipal();
        log.info("getUser 컨트롤러 진입  userId: {}", userId);
        UserDto response = userService.getUserById(userId);
        log.debug("getUser 성공  userDto: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     회원 탈퇴
     jwt  필요
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(Authentication authentication) {
        // 인증 객체에서 현재 로그인한 사용자의 id를 가져옴
        Long userId = (Long) authentication.getPrincipal();
        log.info("deleteUser 컨트롤러 진입  userId: {}", userId);
        userService.deleteUserById(userId);
        log.debug("deleteUser 성공");
        return ResponseEntity.noContent().build();
    }
}