package com.champsoft.catalogservice.catalog.domain;

public class InvalidBookStateException extends RuntimeException {

    public InvalidBookStateException(String message) {
        super(message);
    }
}
