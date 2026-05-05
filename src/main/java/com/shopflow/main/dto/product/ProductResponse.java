package com.shopflow.main.dto.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.shopflow.main.dto.category.CategoryResponse;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String nom;
    private String description;
    private BigDecimal prix;
    private BigDecimal prixPromo;
    private Integer stock;
    private boolean actif;
    private LocalDateTime dateCreation;
    private String sellerName;
    private Long sellerId;
    private List<String> images;
    private List<CategoryResponse> categories;
    private List<Long> categoryIds;
    private List<ProductVariantResponse> variants;
    private Double averageRating;
    private Long reviewCount;
}
