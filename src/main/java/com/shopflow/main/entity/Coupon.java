package com.shopflow.main.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.shopflow.main.listener.FirebaseSyncListener;

@Entity
@EntityListeners(FirebaseSyncListener.class)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Coupon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private CouponType type;

    private BigDecimal valeur;
    private LocalDateTime dateExpiration;
    private Integer usagesMax;
    private Integer usagesActuels = 0;
    private boolean actif = true;
}