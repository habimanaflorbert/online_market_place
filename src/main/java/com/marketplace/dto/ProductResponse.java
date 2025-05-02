package com.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long stock;
    private boolean featured;
    private Long storeId;
    private String storeName;
    private Long categoryId;
    private String categoryName;
    private Double averageRating;
    private Integer reviewCount;
} 