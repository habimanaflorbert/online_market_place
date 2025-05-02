package com.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StoreRequest {
    @NotBlank(message = "Store name is required")
    @Size(min = 3, max = 100, message = "Store name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
} 