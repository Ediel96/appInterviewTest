package com.backend.hamilton.application.port.in;

import com.backend.hamilton.domain.model.Product;

import java.util.List;

/**
 * Input port for retrieving all products from the catalog.
 */
public interface GetProductsUseCase {

    /**
     * Retrieves all available products.
     *
     * @return list of all products
     */
    List<Product> getProducts();
}
