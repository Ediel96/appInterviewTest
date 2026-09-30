package com.backend.hamilton.adapter.out.dummyjson;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO representing a product from DummyJSON API.
 */
public record DummyJsonProductDto(
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
