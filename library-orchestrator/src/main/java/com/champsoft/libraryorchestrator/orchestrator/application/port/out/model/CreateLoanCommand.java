package com.champsoft.libraryorchestrator.orchestrator.application.port.out.model;

import java.time.LocalDate;
import java.util.UUID;

public record CreateLoanCommand(
        UUID memberId,
        UUID bookId,
        int quantity,
        LocalDate startDate,
        LocalDate dueDate
) {
}
