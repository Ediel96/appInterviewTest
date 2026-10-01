package com.backend.hamilton.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO representing a single product for the API.
 */
@Schema(description = "Información del producto")
public record ProductResponse(
        @Schema(description = "Identificador único del producto", example = "1")
        Long id,

        @Schema(description = "Nombre del producto", example = "Essence Mascara Lash Princess")
        String title,

        @Schema(description = "Descripción del producto")
        String description,

        @Schema(description = "Precio del producto en USD", example = "9.99")
        BigDecimal price,

        @Schema(description = "Calificación del producto de 0 a 5", example = "4.5")
        BigDecimal rating,

        @Schema(description = "URL de la imagen miniatura",
                example = "https://cdn.dummyjson.com/product-images/1/thumbnail.jpg")
        String thumbnail,

        @Schema(description = "URLs de las imágenes del producto")
        List<String> images,

        @Schema(description = "Categoría del producto", example = "beauty")
        String category,

        @Schema(description = "Marca del producto; puede ser null", nullable = true, example = "Essence")
        String brand
) {
}
