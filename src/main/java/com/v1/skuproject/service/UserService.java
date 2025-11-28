package com.v1.skuproject.service;

import com.v1.skuproject.config.jwt.JwtProvider;
import com.v1.skuproject.domain.user.User;
import com.v1.skuproject.dto.user.UserRequest;
import com.v1.skuproject.dto.user.UserResponse.UserDto;
import com.v1.skuproject.repository.UserRepository;
import com.v1.skuproject.util.exception.BaseException;
import com.v1.skuproject.util.exception.ErrorCode;
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
        log.info("createUser 서비스 호출  request: {}",
                request.toString());

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
        log.info("createUser 성공  result: userId={}", userId);
        return userId;
    }

    @Transactional(readOnly = true)
    public UserDto login(UserRequest.Login request) {
        log.info("login 서비스 호출  request: {}", request.toString());

        User user = userRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_CREDENTIALS);
        }
        // JWT 생성
        String token = jwtProvider.generateToken(user.getId(), user.getStudentId());

        UserDto dto = UserDto.from(user, token);
        log.info("login 성공  result: {}", dto.toString());
        return dto;
    }
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId){
        log.info("getUserById 서비스 호출  userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        UserDto dto = UserDto.from(user, null);
        log.info("getUserById 성공  result: {}", dto.toString());
        return dto;
    }

    @Transactional
    public void deleteUserById(Long userId){
        log.info("deleteUserById 서비스 호출  userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->new BaseException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
        log.info("deleteUserById 성공  userId: {}", userId);
    }
}
