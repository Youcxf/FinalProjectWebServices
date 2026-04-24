package com.champsoft.borrowingservice.borrowing.domain;

public class InvalidLoanStateException extends RuntimeException {

    public InvalidLoanStateException(String message) {
        super(message);
    }
}
