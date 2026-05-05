package com.shopflow.main.dto.review;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String customerName;
    private Integer note;
    private String commentaire;
    private LocalDateTime dateCreation;
    private boolean approuve;
}
