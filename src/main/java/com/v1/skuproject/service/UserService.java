package com.v1.skuproject.service;

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
        log.info("createUser 서비스 호출  request: {}",
                request.toString());
        if(userRepository.existsByStudentId(request.getStudentId())){
            throw new IllegalArgumentException("이미 존재하는 학번입니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        UserDto dto = UserDto.from(user, null);
        log.info("getUserById 성공  result: {}", dto.toString());
        return dto;
    }

    @Transactional
    public void deleteUserById(Long userId){
        log.info("deleteUserById 서비스 호출  userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        userRepository.delete(user);
        log.info("deleteUserById 성공  userId: {}", userId);
    }
}
