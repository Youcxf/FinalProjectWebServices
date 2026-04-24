package com.champsoft.libraryorchestrator.orchestrator.application;

import java.util.UUID;

public class BorrowingDecisionNotFoundApplicationException extends RuntimeException {

    public BorrowingDecisionNotFoundApplicationException(UUID decisionId) {
        super("Borrowing decision not found: " + decisionId);
    }
}
