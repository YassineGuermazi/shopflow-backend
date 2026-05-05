package com.shopflow.main.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal sousTotal;
    private BigDecimal fraisLivraison;
    private BigDecimal totalTTC;
    private String couponCode;
    private BigDecimal discount;
    private LocalDateTime dateModification;
}