package com.backend.hamilton.adapter.in.web;

import com.backend.hamilton.adapter.in.web.dto.ApiErrorResponse;
import com.backend.hamilton.adapter.in.web.dto.ProductListResponse;
import com.backend.hamilton.adapter.in.web.dto.ProductResponse;
import com.backend.hamilton.application.port.in.GetProductByIdUseCase;
import com.backend.hamilton.application.port.in.GetProductsUseCase;
import com.backend.hamilton.domain.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for product operations.
 * Handles HTTP requests and delegates to use cases.
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product catalog operations")
public class ProductController {

    private final GetProductsUseCase getProductsUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;

    /**
     * Constructor with dependency injection.
     *
     * @param getProductsUseCase use case for retrieving all products
     * @param getProductByIdUseCase use case for retrieving a product by ID
     */
    public ProductController(
            GetProductsUseCase getProductsUseCase,
            GetProductByIdUseCase getProductByIdUseCase) {
        this.getProductsUseCase = getProductsUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
    }

    /**
     * Retrieves all products.
     *
     * @return list of all products with total count
     */
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves the complete list of products from the catalog")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "External service error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "504",
                    description = "External service timeout",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ProductListResponse> getAllProducts() {
        List<Product> products = getProductsUseCase.getProducts();
        ProductListResponse response = ProductWebMapper.toListResponse(products);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the product identifier
     * @return the product details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves detailed information about a specific product")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product ID",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "External service error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "504",
                    description = "External service timeout",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product identifier (must be greater than 0)", required = true, example = "1")
            @PathVariable Long id) {
        Product product = getProductByIdUseCase.getProductById(id);
        ProductResponse response = ProductWebMapper.toResponse(product);
        return ResponseEntity.ok(response);
    }
}
