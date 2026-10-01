package com.backend.hamilton.adapter.in.web.dto;

import com.backend.hamilton.adapter.in.web.validation.CategoryConstraint;
import com.backend.hamilton.adapter.in.web.validation.PageNumberConstraint;
import com.backend.hamilton.adapter.in.web.validation.PageSizeConstraint;
import com.backend.hamilton.adapter.in.web.validation.SearchTermConstraint;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Input DTO describing the query parameters accepted by the product collection endpoint.
 *
 * <p>Every limit enforced here is declared in {@code api.validation}. The numeric
 * boundaries are not repeated in these annotations: {@code OpenApiCustomizerConfig}
 * injects them into the generated document from the same properties bean, so the
 * published contract and the runtime validation cannot drift apart.
 *
 * @param page zero-based page index
 * @param size page size; zero returns the whole catalog
 * @param search free text term matched against the product title
 * @param category exact category filter
 */
@Schema(description = "Parámetros de consulta del catálogo de productos")
public record ProductFilterRequest(
        @PageNumberConstraint
        @Schema(description = "Página solicitada, empezando en 0", example = "0")
        Integer page,

        @PageSizeConstraint
        @Schema(description = "Cantidad de productos por página; 0 devuelve el catálogo completo", example = "20")
        Integer size,

        @SearchTermConstraint
        @Schema(description = "Término de búsqueda libre sobre el nombre del producto", example = "mascara")
        String search,

        @CategoryConstraint
        @Schema(description = "Filtro por categoría exacta", example = "beauty")
        String category
) {
}