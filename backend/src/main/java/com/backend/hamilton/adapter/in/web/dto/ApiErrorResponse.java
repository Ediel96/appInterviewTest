package com.backend.hamilton.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Standard error response for API errors.
 *
 * <p>The {@code status}, {@code code} and {@code message} values are all resolved from
 * {@code api.errors.definitions}, so this schema always mirrors the configured catalog.
 */
@Schema(description = "Información de respuesta de error")
public record ApiErrorResponse(
        @Schema(description = "Código HTTP del error", example = "404")
        int status,

        @Schema(description = "Código estable del error, definido en api.errors.definitions",
                example = "PRODUCT_NOT_FOUND")
        String code,

        @Schema(description = "Descripción del error", example = "Product with ID 999 not found")
        String message,

        @Schema(description = "Ruta de la petición que provocó el error", example = "/api/products/999")
        String path,

        @Schema(description = "Fecha y hora del error en formato ISO-8601",
                example = "2024-01-15T10:30:00Z")
        String timestamp
) {
}