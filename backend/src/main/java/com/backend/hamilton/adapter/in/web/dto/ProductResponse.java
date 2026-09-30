package com.backend.hamilton.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO representing a single product for the API.
 */
@Schema(description = "Product information")
public record ProductResponse(
        @Schema(description = "Unique product identifier", example = "1")
        Long id,

        @Schema(description = "Product name", example = "Essence Mascara Lash Princess")
        String title,

        @Schema(description = "Product description", example = "A popular mascara known for its volumizing effects")
        String description,

        @Schema(description = "Product price in USD", example = "9.99")
        BigDecimal price,

        @Schema(description = "Product rating from 0 to 5", example = "4.5")
        BigDecimal rating,

        @Schema(description = "URL of the product thumbnail image", example = "https://cdn.dummyjson.com/product-images/1/thumbnail.jpg")
        String thumbnail,

        @Schema(description = "List of product image URLs")
        List<String> images,

        @Schema(description = "Product category", example = "beauty")
        String category,

        @Schema(description = "Product brand name", example = "Essence", nullable = true)
        String brand
) {
}
