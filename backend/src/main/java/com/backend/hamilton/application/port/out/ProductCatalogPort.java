package com.backend.hamilton.application.port.out;

import com.backend.hamilton.domain.model.Product;
import com.backend.hamilton.domain.model.ProductQuery;

import java.util.List;

/**
 * Output port for accessing the product catalog.
 * Abstracts the external product catalog source without knowing implementation details.
 */
public interface ProductCatalogPort {

    /**
     * Retrieves all products from the catalog.
     *
     * @return list of all products
     */
    List<Product> findAll();

    /**
     * Returns the products matching the given query.
     *
     * @param query paging and filtering criteria
     * @return the matching products
     */
    List<Product> findAll(ProductQuery query);

    /**
     * Retrieves a product by its ID from the catalog.
     *
     * @param id the product identifier
     * @return the product with the specified ID
     * @throws com.backend.hamilton.domain.exception.ProductNotFoundException if the product is not found
     */
    Product findById(Long id);
}
