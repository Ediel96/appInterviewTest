package com.backend.hamilton.adapter.out.dummyjson;

import com.backend.hamilton.domain.model.ProductQuery;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the query string built for DummyJSON.
 */
class DummyJsonUriBuilderTest {

    private static final String BASE_URL = "https://dummyjson.com";
    private static final String PRODUCTS = "/products";
    private static final int ALL_LIMIT = 0;

    @Test
    void shouldUseConfiguredAllProductsLimitWhenNoFilterIsGiven() {
        String uri = DummyJsonUriBuilder.build(BASE_URL, PRODUCTS, ALL_LIMIT, new ProductQuery(0, 0, null, null));

        assertEquals("https://dummyjson.com/products?limit=0", uri);
    }

    @Test
    void shouldTranslatePageAndSizeIntoSkipAndLimit() {
        String uri = DummyJsonUriBuilder.build(BASE_URL, PRODUCTS, ALL_LIMIT, new ProductQuery(2, 10, null, null));

        assertEquals("https://dummyjson.com/products?skip=20&limit=10", uri);
    }

    @Test
    void shouldForwardSearchAndCategory() {
        String uri = DummyJsonUriBuilder.build(
                BASE_URL, PRODUCTS, ALL_LIMIT, new ProductQuery(0, 0, "mascara", "beauty"));

        assertEquals("https://dummyjson.com/products?limit=0&q=mascara&category=beauty", uri);
    }

    @Test
    void shouldIgnoreBlankFilters() {
        String uri = DummyJsonUriBuilder.build(
                BASE_URL, PRODUCTS, ALL_LIMIT, new ProductQuery(0, 0, "  ", ""));

        assertEquals("https://dummyjson.com/products?limit=0", uri);
    }

    @Test
    void shouldTrimTheSearchTerm() {
        String uri = DummyJsonUriBuilder.build(
                BASE_URL, PRODUCTS, ALL_LIMIT, new ProductQuery(0, 0, "  lipstick  ", null));

        assertEquals("https://dummyjson.com/products?limit=0&q=lipstick", uri);
    }

    @Test
    void shouldUseTheConfiguredBaseUrlAndPath() {
        String uri = DummyJsonUriBuilder.build(
                "http://localhost:9000", "/catalog", 0, new ProductQuery(0, 0, null, null));

        assertEquals("http://localhost:9000/catalog?limit=0", uri);
    }
}