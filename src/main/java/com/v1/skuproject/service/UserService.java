package com.v1.skuproject.service;

import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import com.v1.skuproject.config.jwt.JwtProvider;
import com.v1.skuproject.domain.user.User;
import com.v1.skuproject.dto.user.UserRequest;
import com.v1.skuproject.dto.user.UserResponse.UserDto;
import com.v1.skuproject.repository.UserRepository;
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

    @Transactional
    public Long createUser(UserRequest.SignUp request){

        if(userRepository.existsByStudentId(request.getStudentId())){
            throw new BaseException(ErrorCode.USER_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .studentId(request.getStudentId())
                .name(request.getName())
                .major(request.getMajor())
                .password(encodedPassword)
                .build();

        Long userId = userRepository.save(user).getId();

        log.info("회원가입 성공 userId={}", userId);

        return userId;
    }

    @Transactional(readOnly = true)
    public UserDto login(UserRequest.Login request) {

        User user = userRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_CREDENTIALS);
        }

        // JWT 생성
        String token = jwtProvider.generateToken(user.getId(), user.getStudentId());

        UserDto userDto = UserDto.from(user, token);

        log.info("로그인 성공 userId={}", user.getId());

        return userDto;
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
