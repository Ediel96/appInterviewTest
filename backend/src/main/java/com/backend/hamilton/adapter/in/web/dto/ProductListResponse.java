package com.backend.hamilton.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response DTO representing a list of products with metadata.
 */
@Schema(description = "Product list with total count")
public record ProductListResponse(
        @Schema(description = "List of products")
        List<ProductResponse> products,

        @Schema(description = "Total number of products", example = "194")
        Integer total
) {
}
