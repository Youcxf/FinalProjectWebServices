package com.champsoft.finalprojectwebservices.shared.domain;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
