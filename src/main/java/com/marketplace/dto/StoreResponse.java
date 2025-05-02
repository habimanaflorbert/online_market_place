package com.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponse {
    private Long id;
    private String name;
    private String description;
    private String ownerEmail;
    private Long ownerId;
} 