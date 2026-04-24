package com.champsoft.catalogservice.catalog.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpsertBookRequest(
        @NotBlank(message = "title is required")
        String title,
        @NotBlank(message = "author is required")
        String author,
        @NotNull(message = "status is required")
        BookStatus status,
        @Min(value = 0, message = "availableCopies must be non-negative")
        int availableCopies
) {
}
