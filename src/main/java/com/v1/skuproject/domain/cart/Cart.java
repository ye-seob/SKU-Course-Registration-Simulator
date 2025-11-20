package com.v1.skuproject.domain.cart;

import com.v1.skuproject.domain.room.Room;
import com.v1.skuproject.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Room - Cart 1 : 1
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // Cart -> CartLecture 1:N
    @OneToMany(mappedBy = "cart")
    private List<CartLecture> cartLectures = new ArrayList<>();

}