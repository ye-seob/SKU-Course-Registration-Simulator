package com.v1.skuproject.cart.service;

import com.v1.skuproject.cart.entity.Cart;
import com.v1.skuproject.cart.entity.CartLecture;
import com.v1.skuproject.cart.entity.CartLectureId;
import com.v1.skuproject.cart.repository.CartLectureRepository;
import com.v1.skuproject.cart.repository.CartRepository;
import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import com.v1.skuproject.lecture.entity.Lecture;
import com.v1.skuproject.lecture.repository.LectureRepository;
import com.v1.skuproject.user.entity.User;
import com.v1.skuproject.user.repository.UserRepository;
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
    private final LectureRepository lectureRepository;
    private final CartRepository cartRepository;
    private final CartLectureRepository cartLectureRepository;

    /**
     * 장바구니에 강의 추가
     */
    @Transactional
    public CartLecture addLectureToCart(Long userId, Long lectureId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    cartRepository.save(newCart);

                    log.info("장바구니 신규 생성 userId={}", userId);
                    return newCart;
                });
        // 장바구니 강의 수 확인
        if (cartLectureRepository.countByCart(cart) >= 10) {
            log.warn("장바구니 최대 강의 수 초과 userId={}", userId);
            throw new BaseException(ErrorCode.CART_LECTURE_LIMIT_EXCEEDED);
        }
        // 이미 담긴 강의인지 확인
        if (cartLectureRepository.findByCartAndLecture(cart, lecture).isPresent()) {
            log.warn("장바구니 중복 추가 시도 userId={} lectureId={}", userId, lectureId);
            throw new BaseException(ErrorCode.CART_LECTURE_ALREADY_EXISTS);
        }

        CartLectureId id = new CartLectureId(cart.getId(), lecture.getId());

        CartLecture cartLecture = CartLecture.builder()
                .id(id)
                .cart(cart)
                .lecture(lecture)
                .addedAt(LocalDateTime.now())
                .build();

        CartLecture saved = cartLectureRepository.save(cartLecture);

        log.info("장바구니 강의 추가 성공 userId={} lectureId={}", userId, lectureId);

        return saved;
    }

    /**
     * 장바구니에서 강의 제거
     */
    @Transactional
    public void removeLectureFromCart(Long userId, Long lectureId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.warn("장바구니 삭제 시도 - 장바구니 없음 userId={}", userId);
                    return new BaseException(ErrorCode.CART_NOT_FOUND);
                });

        boolean exists = cartLectureRepository.findByCartAndLecture(cart, lecture).isPresent();
        if (!exists) {
            log.warn("장바구니 강의 삭제 실패 - 강의 없음 userId={} lectureId={}", userId, lectureId);
            throw new BaseException(ErrorCode.CART_LECTURE_NOT_FOUND);
        }

        cartLectureRepository.deleteByCartAndLecture(cart, lecture);

        log.info("장바구니 강의 삭제 성공 userId={} lectureId={}", userId, lectureId);
    }

    /**
     * 장바구니 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CartLecture> getCartLectures(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Optional<Cart> cartOptional = cartRepository.findByUser(user);

        // 장바구니가 없으면 정상 케이스로 빈 리스트 반환
        if (cartOptional.isEmpty()) {
            log.info("장바구니 조회 - 비어있음 userId={}", userId);
            return List.of();
        }

        List<CartLecture> cartLectures =
                cartLectureRepository.findByCart(cartOptional.get());

        log.info("장바구니 조회 userId={} count={}", userId, cartLectures.size());

        return cartLectures;
    }
}