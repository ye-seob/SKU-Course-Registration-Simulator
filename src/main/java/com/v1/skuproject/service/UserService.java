package com.v1.skuproject.service;

import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import com.v1.skuproject.config.jwt.JwtProvider;
import com.v1.skuproject.domain.user.Role;
import com.v1.skuproject.domain.user.User;
import com.v1.skuproject.dto.user.UserRequest.GuestLoginRequest;
import com.v1.skuproject.dto.user.UserRequest.Login;
import com.v1.skuproject.dto.user.UserRequest.SignUp;
import com.v1.skuproject.dto.user.UserResponse.UserDto;
import com.v1.skuproject.repository.UserRepository;
import com.v1.skuproject.util.TimeChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TimeChecker timeChecker;

    @Transactional
    public Long createUser(SignUp request){

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
    public UserDto login(Login request) {

        if(request.getLoginMode().equals("ENROLL")){
            timeChecker.validateLogin();
        }

        User user = userRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // JWT 생성
        String token = jwtProvider.generateToken(user.getId(), user.getStudentId(),user.getRole());

        UserDto userDto = UserDto.from(user, token);

        log.info("로그인 성공 userId={}", user.getId());

        return userDto;
    }

    @Transactional
    public UserDto guestLogin(GuestLoginRequest request) {
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

        return UserDto.from(guest, token);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        UserDto userDto = UserDto.from(user, null);

        return userDto;
    }

    @Transactional
    public void deleteUserById(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() ->new BaseException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);

        log.info("회원 삭제 성공  userId: {}", userId);
    }
}
