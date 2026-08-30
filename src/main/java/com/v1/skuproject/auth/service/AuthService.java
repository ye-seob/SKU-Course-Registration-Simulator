package com.v1.skuproject.auth.service;

import com.v1.skuproject.auth.dto.UserRequest;
import com.v1.skuproject.auth.dto.UserResponse;
import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import com.v1.skuproject.config.jwt.JwtProvider;
import com.v1.skuproject.user.entity.Role;
import com.v1.skuproject.user.entity.User;
import com.v1.skuproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public Long createUser(UserRequest.SignUp request){

        if(userRepository.existsByStudentId(request.getStudentId())){
            throw new BaseException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .studentId(request.getStudentId())
                .name(request.getName())
                .grade(request.getGrade())
                .role(Role.ROLE_USER)
                .major(request.getMajor())
                .build();

        Long userId = userRepository.save(user).getId();

        log.info("회원가입 성공 userId={}", userId);

        return userId;
    }

    @Transactional(readOnly = true)
    public UserResponse.UserDto login(UserRequest.Login request) {


        User user = userRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // JWT 생성
        String token = jwtProvider.generateToken(user.getId(), user.getStudentId(),user.getRole());

        UserResponse.UserDto userDto = UserResponse.UserDto.from(user, token);

        log.info("로그인 성공 userId={}", user.getId());

        return userDto;
    }

    @Transactional
    public UserResponse.UserDto guestLogin(UserRequest.GuestLoginRequest request) {
        User guest = User.builder()
                .studentId("guest-" + System.currentTimeMillis())
                .name("비회원")
                .major(request.getMajor())
                .grade(1)
                .role(Role.ROLE_GUEST)
                .build();

        userRepository.save(guest);

        String token = jwtProvider.generateToken(
                guest.getId(),
                guest.getStudentId(),
                guest.getRole()
        );

        return UserResponse.UserDto.from(guest, token);
    }

}
