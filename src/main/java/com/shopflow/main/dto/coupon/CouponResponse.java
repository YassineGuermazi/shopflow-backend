package com.shopflow.main.dto.coupon;

import com.shopflow.main.entity.CouponType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CouponResponse {
    private Long id;
    private String code;
    private CouponType type;
    private BigDecimal valeur;
    private LocalDateTime dateExpiration;
    private Integer usagesMax;
    private Integer usagesActuels;
    private boolean actif;
    private boolean valid;
}