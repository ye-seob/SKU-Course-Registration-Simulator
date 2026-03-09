package com.v1.skuproject.user.service;

import com.v1.skuproject.auth.dto.UserResponse.UserDto;
import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import com.v1.skuproject.user.entity.User;
import com.v1.skuproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

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

    @Transactional
    public void deleteGuestUsers() {

        LocalDateTime time = LocalDateTime.now().minusHours(1);

        int deleted = userRepository.deleteGuestUsers(time);

        log.info("삭제된 비회원 유저 = {}",deleted);
    }
}
