package com.v1.skuproject.service;

import com.v1.skuproject.domain.user.User;
import com.v1.skuproject.dto.user.UserRequest;
import com.v1.skuproject.dto.user.UserResponse.UserDto;
import com.v1.skuproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long createUser(UserRequest.SignUp request){
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

        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public UserDto login(UserRequest.Login request) {
        User user = userRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return UserDto.from(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return UserDto.from(user);
    }

    @Transactional
    public void deleteUserById(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        userRepository.deleteById(userId);
    }
}
