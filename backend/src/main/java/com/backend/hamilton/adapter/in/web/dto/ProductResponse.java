package com.backend.hamilton.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO representing a single product for the API.
 */
public record ProductResponse(
        Long id,
        String title,
        String description,
        BigDecimal price,
        BigDecimal rating,
        String thumbnail,
        List<String> images,
        String category,
        String brand
) {
}
