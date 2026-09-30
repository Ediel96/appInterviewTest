package com.backend.hamilton.adapter.out.dummyjson;

import com.backend.hamilton.domain.model.Product;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Mapper to convert DummyJSON DTOs to domain model.
 */
public class DummyJsonProductMapper {

    /**
     * Converts a DummyJsonProductDto to a Product domain model.
     *
     * @param dto the DTO from DummyJSON API
     * @return the domain Product
     */
    public static Product toDomain(DummyJsonProductDto dto) {
        return new Product(
                dto.id(),
                dto.title(),
                dto.description(),
                safePrice(dto.price()),
                safeRating(dto.rating()),
                dto.thumbnail(),
                safeImages(dto.images()),
                dto.category(),
                dto.brand()  // brand is allowed to be null
        );
    }

    /**
     * Safely converts price, defaulting to zero if null.
     */
    private static BigDecimal safePrice(BigDecimal price) {
        return price != null ? price : BigDecimal.ZERO;
    }

    /**
     * Safely converts rating, defaulting to zero if null.
     */
    private static BigDecimal safeRating(BigDecimal rating) {
        return rating != null ? rating : BigDecimal.ZERO;
    }

    /**
     * Safely converts images list, returning empty list if null.
     */
    private static List<String> safeImages(List<String> images) {
        return images != null ? images : Collections.emptyList();
    }
}
