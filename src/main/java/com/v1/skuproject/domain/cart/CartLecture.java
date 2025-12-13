package com.v1.skuproject.domain.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.v1.skuproject.domain.lecture.Lecture;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_lectures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class CartLecture {
    @EmbeddedId
    private CartLectureId id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lectureId")
    private Lecture lecture;

    @CreatedDate
    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;
}

