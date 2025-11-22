package com.v1.skuproject.domain.cart;

import com.v1.skuproject.domain.lecture.Lecture;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_lectures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CartLecture {
    @EmbeddedId
    private CartLectureId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lectureId")
    private Lecture lecture;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;
}

