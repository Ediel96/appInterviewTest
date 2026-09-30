package com.backend.hamilton.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Product domain model representing a product in the catalog.
 * Uses defensive copies for the images list to maintain immutability.
 */
public record Product(
        Long id,
        String title,
        String description,
        BigDecimal price,
        BigDecimal rating,
        String thumbnail,
        List<String> images,
        String category,
        String brand
) {
    /**
     * Compact constructor with validation and defensive copies.
     */
    public Product {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(title, "title cannot be null");
        Objects.requireNonNull(description, "description cannot be null");
        Objects.requireNonNull(price, "price cannot be null");
        Objects.requireNonNull(rating, "rating cannot be null");
        Objects.requireNonNull(thumbnail, "thumbnail cannot be null");
        Objects.requireNonNull(images, "images cannot be null");
        Objects.requireNonNull(category, "category cannot be null");

        // Defensive copy of the images list
        images = List.copyOf(images);
    }

    /**
     * Returns a defensive copy of the images list.
     *
     * @return an immutable copy of the images list
     */
    @Override
    public List<String> images() {
        return new ArrayList<>(images);
    }
}
