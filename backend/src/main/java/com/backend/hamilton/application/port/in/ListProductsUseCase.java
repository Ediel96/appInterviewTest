package com.backend.hamilton.application.port.in;

import com.backend.hamilton.domain.model.Product;
import com.backend.hamilton.domain.model.ProductQuery;

import java.util.List;

/**
 * Input port for retrieving a page of the product catalog.
 */
public interface ListProductsUseCase {

    /**
     * Returns the products matching the given query.
     *
     * @param query paging and filtering criteria; a default query retrieves everything
     * @return the matching products
     */
    List<Product> list(ProductQuery query);
}