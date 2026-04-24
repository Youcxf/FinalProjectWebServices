package com.champsoft.catalogservice.catalog.domain;

public class DuplicateBookTitleException extends RuntimeException {

    public DuplicateBookTitleException(String title) {
        super("Book title already exists: " + title);
    }
}
