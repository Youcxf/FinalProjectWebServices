package com.champsoft.catalogservice.catalog.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CatalogDomainTests {

    @Test
    void createsBookResponse() {
        UUID id = UUID.randomUUID();
        BookResponse response = new BookResponse(id, "Clean Code", "Robert Martin", BookStatus.AVAILABLE, 2);

        assertEquals(id, response.id());
        assertEquals(BookStatus.AVAILABLE, response.status());
    }

    @Test
    void duplicateTitleExceptionKeepsMessage() {
        DuplicateBookTitleException exception = assertThrows(
                DuplicateBookTitleException.class,
                () -> {
                    throw new DuplicateBookTitleException("Clean Code");
                }
        );

        assertEquals("Book title already exists: Clean Code", exception.getMessage());
    }

    @Test
    void invalidStateExceptionKeepsMessage() {
        InvalidBookStateException exception = assertThrows(
                InvalidBookStateException.class,
                () -> {
                    throw new InvalidBookStateException("bad book");
                }
        );

        assertEquals("bad book", exception.getMessage());
    }
}
