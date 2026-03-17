package com.buy01.product_service.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductResponse(
    String id,
    String name,
    String description,
    BigDecimal price,
    Integer stockQuantity,
    String category,
    String sellerId,
    List<String> mediaIds,
    LocalDateTime createdAt
) {}