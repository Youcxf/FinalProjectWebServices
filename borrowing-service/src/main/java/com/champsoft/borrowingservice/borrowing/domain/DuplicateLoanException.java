package com.champsoft.borrowingservice.borrowing.domain;

public class DuplicateLoanException extends RuntimeException {

    public DuplicateLoanException(String message) {
        super(message);
    }
}
