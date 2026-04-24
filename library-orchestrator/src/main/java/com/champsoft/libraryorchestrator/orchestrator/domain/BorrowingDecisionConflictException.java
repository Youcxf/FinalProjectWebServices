package com.champsoft.libraryorchestrator.orchestrator.domain;

public class BorrowingDecisionConflictException extends RuntimeException {

    public BorrowingDecisionConflictException(String message) {
        super(message);
    }
}
