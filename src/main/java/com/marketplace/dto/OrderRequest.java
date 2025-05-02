package com.marketplace.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotNull(message = "Store ID is required")
    private Long storeId;

    @NotEmpty(message = "Order items are required")
    @Valid
    private List<OrderItemRequest> items;
} 