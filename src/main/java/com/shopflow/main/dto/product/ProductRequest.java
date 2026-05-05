package com.shopflow.main.dto.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    @NotBlank
    private String nom;

    private String description;

    @NotNull @DecimalMin("0.0")
    private BigDecimal prix;

    @DecimalMin("0.0")
    private BigDecimal prixPromo;

    @NotNull @Min(0)
    private Integer stock;

    private List<String> images;
    private List<Long> categoryIds;
}