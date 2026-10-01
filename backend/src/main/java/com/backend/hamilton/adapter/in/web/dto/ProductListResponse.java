package com.backend.hamilton.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response DTO representing a list of products with metadata.
 */
@Schema(description = "Lista de productos con el total de resultados")
public record ProductListResponse(
        @Schema(description = "Productos recuperados del catálogo")
        List<ProductResponse> products,

        @Schema(description = "Cantidad de productos incluida en products", example = "194")
        Integer total
) {
}
