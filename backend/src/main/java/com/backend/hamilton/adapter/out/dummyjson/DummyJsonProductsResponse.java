package com.backend.hamilton.adapter.out.dummyjson;

import java.util.List;

/**
 * DTO representing the paginated response from DummyJSON products endpoint.
 */
public record DummyJsonProductsResponse(
        List<DummyJsonProductDto> products,
        Integer total,
        Integer skip,
        Integer limit
) {
}
