package com.v1.skuproject.service;

import com.v1.skuproject.domain.cart.Cart;
import com.v1.skuproject.domain.cart.CartLecture;
import com.v1.skuproject.domain.cart.CartLectureId;
import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.user.User;
import com.v1.skuproject.repository.CartLectureRepository;
import com.v1.skuproject.repository.CartRepository;
import com.v1.skuproject.repository.LectureRepository;
import com.v1.skuproject.repository.UserRepository;
import com.v1.skuproject.util.exception.BaseException;
import com.v1.skuproject.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final LectureRepository lectureRepository; // Lecture 객체 조회를 위해 필요
    private final CartRepository cartRepository;
    private final CartLectureRepository cartLectureRepository;

    @Transactional
    public CartLecture addLectureToCart(Long userId, Long lectureId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        // 2. 장바구니(Cart) 조회 또는 생성 (User당 1개)
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();

                    return cartRepository.save(newCart);
                });


        Optional<CartLecture> existingCartLecture = cartLectureRepository.findByCartAndLecture(cart, lecture);

        if (existingCartLecture.isPresent()) {
            throw new BaseException(ErrorCode.INVALID_REQUEST);
        }



        CartLectureId id = new CartLectureId(cart.getId(), lecture.getId());
        CartLecture cartLecture = CartLecture.builder()
                .id(id)
                .cart(cart)
                .lecture(lecture)
                .addedAt(LocalDateTime.now())
                .build();

        return cartLectureRepository.save(cartLecture);
    }


    @Transactional
    public void removeLectureFromCart(Long userId, Long lectureId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> {
                    throw new BaseException(ErrorCode.INVALID_REQUEST);
                });


        cartLectureRepository.deleteByCartAndLecture(cart, lecture);
    }


    @Transactional(readOnly = true)
    public List<CartLecture> getCartLectures(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Optional<Cart> cartOptional = cartRepository.findByUser(user);

        if (cartOptional.isEmpty()) {
            return List.of();
        }

        Cart cart = cartOptional.get();
        
        List<CartLecture> cartLectures = cartLectureRepository.findByCart(cart);

        return cartLectures;
    }
}
