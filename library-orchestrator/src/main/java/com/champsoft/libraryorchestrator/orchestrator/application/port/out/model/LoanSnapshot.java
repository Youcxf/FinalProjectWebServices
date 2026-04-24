package com.champsoft.libraryorchestrator.orchestrator.application.port.out.model;

import java.util.UUID;

public record LoanSnapshot(
        UUID id,
        UUID memberId,
        UUID bookId,
        int quantity,
        String status
) {
}
