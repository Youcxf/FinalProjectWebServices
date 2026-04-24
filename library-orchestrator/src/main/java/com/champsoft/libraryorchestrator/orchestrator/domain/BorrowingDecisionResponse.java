package com.champsoft.libraryorchestrator.orchestrator.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BorrowingDecisionResponse(
        UUID id,
        UUID memberId,
        UUID bookId,
        int quantity,
        LocalDate startDate,
        LocalDate dueDate,
        BorrowingDecisionStatus status,
        String decisionReason,
        UUID loanId,
        Instant createdAt,
        Instant updatedAt
) {
}
