package com.shopflow.main.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank
    private String nom;

    private String description;
    private Long parentId;
}