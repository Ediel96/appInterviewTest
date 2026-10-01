package com.backend.hamilton.adapter.in.web;

import com.backend.hamilton.application.port.in.GetProductByIdUseCase;
import com.backend.hamilton.application.port.in.ListProductsUseCase;
import com.backend.hamilton.adapter.in.web.dto.ApiErrorResponse;
import com.backend.hamilton.adapter.in.web.dto.ProductFilterRequest;
import com.backend.hamilton.adapter.in.web.dto.ProductListResponse;
import com.backend.hamilton.adapter.in.web.dto.ProductResponse;
import com.backend.hamilton.adapter.in.web.validation.ProductIdConstraint;
import com.backend.hamilton.configuration.properties.ValidationRulesProperties;
import com.backend.hamilton.domain.model.Product;
import com.backend.hamilton.domain.model.ProductQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for product operations.
 * Handles HTTP requests and delegates to use cases.
 *
 * <p>The base path and every validation boundary come from the externalised
 * configuration, so nothing about the public contract is hardcoded here.
 */
@RestController
@RequestMapping("${api.docs.endpoints.products-base-path}")
@Tag(name = "Products", description = "Operaciones del catálogo de productos")
public class ProductController {

    private final ListProductsUseCase listProductsUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final ValidationRulesProperties validationRules;

    /**
     * Constructor with dependency injection.
     *
     * @param listProductsUseCase use case for retrieving a page of the catalog
     * @param getProductByIdUseCase use case for retrieving a product by ID
     * @param validationRules configured validation boundaries, used to apply defaults
     */
    public ProductController(
            ListProductsUseCase listProductsUseCase,
            GetProductByIdUseCase getProductByIdUseCase,
            ValidationRulesProperties validationRules) {
        this.listProductsUseCase = listProductsUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.validationRules = validationRules;
    }

    /**
     * Retrieves the products matching the supplied filters.
     *
     * @param filter paging and filtering criteria, validated against the configured boundaries
     * @return the matching products with the total count of the page
     */
    @GetMapping
    @Operation(
            summary = "Obtener todos los productos",
            description = """
                    Recupera los productos del catálogo aplicando paginación y filtros opcionales.
                    Los parámetros omitidos toman los valores por defecto declarados en api.validation.
                    Un tamaño de página igual a 0 devuelve el catálogo completo e ignora la página.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos recuperados correctamente",
                    content = @Content(schema = @Schema(implementation = ProductListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Algún parámetro de consulta viola las restricciones de validación",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Error del servicio externo",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "504",
                    description = "Tiempo de espera agotado del servicio externo",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ProductListResponse> getAllProducts(
            @ParameterObject @Valid ProductFilterRequest filter) {
        ProductQuery query = new ProductQuery(
                validationRules.resolvePage(filter.page()),
                validationRules.resolveSize(filter.size()),
                filter.search(),
                filter.category());

        List<Product> products = listProductsUseCase.list(query);
        return ResponseEntity.ok(ProductWebMapper.toListResponse(products));
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the product identifier
     * @return the product details
     */
    @GetMapping("${api.docs.endpoints.product-id-path}")
    @Operation(
            summary = "Obtener producto por ID",
            description = "Recupera los detalles del producto cuyo ID cumple el mínimo declarado en api.validation."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto recuperado correctamente",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID de producto inválido o con formato incorrecto",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Error del servicio externo",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "504",
                    description = "Tiempo de espera agotado del servicio externo",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(
                    description = "Identificador del producto; el mínimo válido es el declarado en api.validation",
                    example = "1")
            @ProductIdConstraint
            @PathVariable Long id) {
        Product product = getProductByIdUseCase.getProductById(id);
        return ResponseEntity.ok(ProductWebMapper.toResponse(product));
    }
}