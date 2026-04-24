package com.champsoft.catalogservice.catalog.domain;

import java.util.UUID;

public record BookResponse(
        UUID id,
        String title,
        String author,
        BookStatus status,
        int availableCopies
) {
}
