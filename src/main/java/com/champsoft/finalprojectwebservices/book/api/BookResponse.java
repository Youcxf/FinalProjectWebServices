package com.champsoft.finalprojectwebservices.book.api;

import com.champsoft.finalprojectwebservices.book.domain.BookStatus;

import java.util.UUID;

public record BookResponse(
        UUID id,
        String title,
        String author,
        BookStatus status,
        int availableCopies
) {
}
