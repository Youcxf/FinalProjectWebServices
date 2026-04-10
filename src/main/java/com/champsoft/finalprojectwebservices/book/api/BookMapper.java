package com.champsoft.finalprojectwebservices.book.api;

import com.champsoft.finalprojectwebservices.book.domain.Book;

import java.util.UUID;

public final class BookMapper {

    private BookMapper() {
    }

    public static Book toDomain(UUID id, BookRequest request) {
        return new Book(id, request.title(), request.author(), request.status(), request.availableCopies());
    }

    public static BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getStatus(), book.getAvailableCopies());
    }
}
