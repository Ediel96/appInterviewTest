package com.backend.hamilton.adapter.in.web;

import com.backend.hamilton.adapter.in.web.dto.ProductListResponse;
import com.backend.hamilton.adapter.in.web.dto.ProductResponse;
import com.backend.hamilton.application.port.in.GetProductByIdUseCase;
import com.backend.hamilton.application.port.in.GetProductsUseCase;
import com.backend.hamilton.domain.model.Product;
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
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = getProductByIdUseCase.getProductById(id);
        ProductResponse response = ProductWebMapper.toResponse(product);
        return ResponseEntity.ok(response);
    }
}
