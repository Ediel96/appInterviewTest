package com.backend.hamilton.application.port.in;

import com.backend.hamilton.domain.model.Product;

/**
 * Input port for retrieving a product by its identifier.
 */
public interface GetProductByIdUseCase {

    /**
     * Retrieves a product by its ID.
     *
     * @param id the product identifier
     * @return the product with the specified ID
     * @throws com.backend.hamilton.domain.exception.InvalidProductIdException if the ID is invalid
     * @throws com.backend.hamilton.domain.exception.ProductNotFoundException if the product is not found
     */
    Product getProductById(Long id);
}
