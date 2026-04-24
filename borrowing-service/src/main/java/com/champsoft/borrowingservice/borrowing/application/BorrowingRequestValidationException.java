package com.champsoft.borrowingservice.borrowing.application;

public class BorrowingRequestValidationException extends RuntimeException {

    public BorrowingRequestValidationException(String message) {
        super(message);
    }
}
