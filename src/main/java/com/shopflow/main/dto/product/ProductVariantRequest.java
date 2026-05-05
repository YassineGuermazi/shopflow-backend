package com.shopflow.main.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantRequest {
    @NotBlank private String attribut;
    @NotBlank private String valeur;

    @NotNull
    private Integer stockSupplementaire;

    private BigDecimal prixDelta;
}