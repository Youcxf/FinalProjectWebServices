package com.champsoft.catalogservice.catalog.application;

import java.util.UUID;

public class BookNotFoundApplicationException extends RuntimeException {

    public BookNotFoundApplicationException(UUID bookId) {
        super("Book not found: " + bookId);
    }
}
