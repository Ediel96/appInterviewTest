package com.backend.hamilton.application.service;

import com.backend.hamilton.application.port.in.GetProductByIdUseCase;
import com.backend.hamilton.application.port.in.GetProductsUseCase;
import com.backend.hamilton.application.port.in.ListProductsUseCase;
import com.backend.hamilton.application.port.out.ProductCatalogPort;
import com.backend.hamilton.domain.exception.ErrorCode;
import com.backend.hamilton.domain.exception.InvalidProductIdException;
import com.backend.hamilton.domain.model.Product;
import com.backend.hamilton.domain.model.ProductQuery;

import java.util.List;
import java.util.Map;

/**
 * Application service implementing product use cases.
 * Orchestrates product operations using the output port.
 */
public class ProductService implements GetProductsUseCase, GetProductByIdUseCase, ListProductsUseCase {

    private final ProductCatalogPort productCatalogPort;

    /**
     * Constructor with dependency injection.
     *
     * @param productCatalogPort the output port for product catalog operations
     */
    public ProductService(ProductCatalogPort productCatalogPort) {
        this.productCatalogPort = productCatalogPort;
    }

    @Override
    public List<Product> getProducts() {
        return productCatalogPort.findAll();
    }

    @Override
    public List<Product> list(ProductQuery query) {
        return productCatalogPort.findAll(query);
    }

    @Override
    public Product getProductById(Long id) {
        validateProductId(id);
        return productCatalogPort.findById(id);
    }

    private void validateProductId(Long id) {
        if (id == null) {
            throw new InvalidProductIdException(ErrorCode.INVALID_PRODUCT_ID, Map.of("reason", "null"));
        }
        if (id <= 0) {
            throw new InvalidProductIdException(ErrorCode.INVALID_PRODUCT_ID, Map.of("received", String.valueOf(id)));
        }
    }
}