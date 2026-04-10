package com.champsoft.finalprojectwebservices.shared.domain;

public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }
}

