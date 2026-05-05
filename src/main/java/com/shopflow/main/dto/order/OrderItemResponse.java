package com.shopflow.main.dto.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long variantId;
    private String variantDetails;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal sousTotal;
}