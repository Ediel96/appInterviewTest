package com.backend.hamilton.domain.model;

/**
 * Paging and filtering criteria for catalog queries.
 *
 * <p>Pure domain value object: no validation is performed here, the inbound
 * adapter is responsible for rejecting values outside the configured boundaries
 * before this object is created.
 *
 * @param page zero-based page index
 * @param size page size; zero means "no limit", retrieve everything
 * @param search free text term matched against the product title, may be null
 * @param category exact category filter, may be null
 */
public record ProductQuery(
        int page,
        int size,
        String search,
        String category
) {

    /**
     * @return true when the caller asked for a bounded page
     */
    public boolean isPagingRequested() {
        return size > 0;
    }
}