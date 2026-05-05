package com.shopflow.main.dto.category;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String nom;
    private String description;
    private Long parentId;
    private List<CategoryResponse> children;
}