package com.shopflow.main.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private Long variantId;
    private String variantDetails;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal sousTotal;
}
