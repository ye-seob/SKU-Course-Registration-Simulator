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
        log.info("장바구니 담기 서비스 진입 - userId: {}, lectureId: {}", userId, lectureId);


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        // 2. 장바구니(Cart) 조회 또는 생성 (User당 1개)
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    log.debug("새로운 장바구니 생성: {}", newCart);
                    return cartRepository.save(newCart);
                });


        Optional<CartLecture> existingCartLecture = cartLectureRepository.findByCartAndLecture(cart, lecture);

        if (existingCartLecture.isPresent()) {
            log.warn("이미 장바구니에 담긴 강의입니다. 예외 발생 - userId: {}, lectureId: {}", userId, lectureId);

            throw new BaseException(ErrorCode.INVALID_REQUEST);
        }



        CartLectureId id = new CartLectureId(cart.getId(), lecture.getId());
        CartLecture cartLecture = CartLecture.builder()
                .id(id)
                .cart(cart)
                .lecture(lecture)
                .addedAt(LocalDateTime.now())
                .build();

        log.info("장바구니에 강의 담기 성공 - CartLecture: {}", cartLecture);
        return cartLectureRepository.save(cartLecture);
    }


    @Transactional
    public void removeLectureFromCart(Long userId, Long lectureId) {
        log.info("장바구니 강의 삭제 서비스 진입 - userId: {}, lectureId: {}", userId, lectureId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.warn("장바구니가 존재하지 않아 삭제할 수 없습니다. - userId: {}", userId);
                    throw new BaseException(ErrorCode.INVALID_REQUEST);
                });


        cartLectureRepository.deleteByCartAndLecture(cart, lecture);
        log.info("장바구니 강의 삭제 성공 - userId: {}, lectureId: {}", userId, lectureId);
    }


    @Transactional(readOnly = true)
    public List<CartLecture> getCartLectures(Long userId) {
        log.info("장바구니 조회 서비스 진입 - userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Optional<Cart> cartOptional = cartRepository.findByUser(user);

        if (cartOptional.isEmpty()) {
            log.info("해당 유저의 장바구니가 존재하지 않습니다. - userId: {}", userId);
            return List.of();
        }

        Cart cart = cartOptional.get();
        
        List<CartLecture> cartLectures = cartLectureRepository.findByCart(cart);
        log.info("장바구니 조회 성공. 담긴 강의 수: {}", cartLectures.size());

        return cartLectures;
    }
}
