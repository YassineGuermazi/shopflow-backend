package com.shopflow.main.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @NotNull
    private Long addressId;
}