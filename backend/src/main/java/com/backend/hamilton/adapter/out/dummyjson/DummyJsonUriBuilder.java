package com.backend.hamilton.adapter.out.dummyjson;

import com.backend.hamilton.domain.model.ProductQuery;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * Builds the DummyJSON catalog URI for a query.
 *
 * <p>Mapping rules: a query without filters uses the configured all-products limit,
 * while a bounded query translates page/size into skip/limit and forwards the
 * free text term as {@code q} and the category as {@code category}.
 */
final class DummyJsonUriBuilder {

    private DummyJsonUriBuilder() {
    }

    static String build(String baseUrl, String productsPath, Integer allProductsLimit, ProductQuery query) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl).path(productsPath);

        if (!query.isPagingRequested() && isBlank(query.search()) && isBlank(query.category())) {
            return builder.queryParam("limit", allProductsLimit).toUriString();
        }

        if (query.isPagingRequested()) {
            builder.queryParam("skip", query.page() * query.size());
            builder.queryParam("limit", query.size());
        } else {
            builder.queryParam("limit", allProductsLimit);
        }

        optionalText(query.search()).ifPresent(term -> builder.queryParam("q", term));
        optionalText(query.category()).ifPresent(category -> builder.queryParam("category", category));

        return builder.toUriString();
    }

    private static Optional<String> optionalText(String value) {
        return isBlank(value) ? Optional.empty() : Optional.of(value.trim());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}