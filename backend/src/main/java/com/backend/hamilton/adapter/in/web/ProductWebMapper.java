package com.backend.hamilton.adapter.in.web;

import com.backend.hamilton.adapter.in.web.dto.ProductListResponse;
import com.backend.hamilton.adapter.in.web.dto.ProductResponse;
import com.backend.hamilton.domain.model.Product;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert domain models to web DTOs.
 */
public class ProductWebMapper {

    /**
     * Converts a Product domain model to a ProductResponse DTO.
     *
     * @param product the domain model
     * @return the response DTO
     */
    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.id(),
                product.title(),
                product.description(),
                product.price(),
                product.rating(),
                product.thumbnail(),
                product.images(),
                product.category(),
                product.brand()
        );
    }

    /**
     * Converts a list of Product domain models to a ProductListResponse DTO.
     *
     * @param products the list of domain models
     * @return the list response DTO with total count
     */
    public static ProductListResponse toListResponse(List<Product> products) {
        List<ProductResponse> productResponses = products.stream()
                .map(ProductWebMapper::toResponse)
                .collect(Collectors.toList());

        return new ProductListResponse(
                productResponses,
                productResponses.size()
        );
    }
}
