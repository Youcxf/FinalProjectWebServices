package com.champsoft.libraryorchestrator.orchestrator.domain;

public class BorrowingRuleViolationException extends RuntimeException {

    public BorrowingRuleViolationException(String message) {
        super(message);
    }
}
