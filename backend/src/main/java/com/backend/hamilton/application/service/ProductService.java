package com.backend.hamilton.application.service;

import com.backend.hamilton.application.port.in.GetProductByIdUseCase;
import com.backend.hamilton.application.port.in.GetProductsUseCase;
import com.backend.hamilton.application.port.out.ProductCatalogPort;
import com.backend.hamilton.domain.exception.InvalidProductIdException;
import com.backend.hamilton.domain.model.Product;

import java.util.List;

/**
 * Application service implementing product use cases.
 * Orchestrates product operations using the output port.
 */
public class ProductService implements GetProductsUseCase, GetProductByIdUseCase {

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
    public Product getProductById(Long id) {
        validateProductId(id);
        return productCatalogPort.findById(id);
    }

    private void validateProductId(Long id) {
        if (id == null) {
            throw new InvalidProductIdException("Product ID cannot be null");
        }
        if (id <= 0) {
            throw new InvalidProductIdException("Product ID must be greater than zero");
        }
    }
}
