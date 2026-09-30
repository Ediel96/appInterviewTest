package com.backend.hamilton.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Standard error response for API errors.
 */
@Schema(description = "Error response information")
public record ApiErrorResponse(
        @Schema(description = "HTTP status code", example = "404")
        int status,

        @Schema(description = "Error message", example = "Product not found")
        String message,

        @Schema(description = "Timestamp of the error", example = "2024-01-15T10:30:00Z")
        String timestamp
) {
}
