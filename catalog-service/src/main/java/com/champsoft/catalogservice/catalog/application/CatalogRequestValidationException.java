package com.champsoft.catalogservice.catalog.application;

public class CatalogRequestValidationException extends RuntimeException {

    public CatalogRequestValidationException(String message) {
        super(message);
    }
}
