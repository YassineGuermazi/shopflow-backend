package com.shopflow.main.dto.review;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull
    private Long productId;

    @NotNull @Min(1) @Max(5)
    private Integer note;

    @NotBlank
    private String commentaire;
}