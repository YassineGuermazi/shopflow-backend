package com.shopflow.main.dto.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductVariantResponse {
    private Long id;
    private String attribut;
    private String valeur;
    private Integer stockSupplementaire;
    private BigDecimal prixDelta;
}