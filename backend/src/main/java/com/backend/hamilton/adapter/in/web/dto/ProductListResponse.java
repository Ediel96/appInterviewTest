package com.backend.hamilton.adapter.in.web.dto;

import java.util.List;

/**
 * Response DTO representing a list of products with metadata.
 */
public record ProductListResponse(
        List<ProductResponse> products,
        Integer total
) {
}
