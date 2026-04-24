package com.champsoft.libraryorchestrator.orchestrator.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record EvaluateBorrowingRequest(
        @NotNull(message = "memberId is required")
        UUID memberId,
        @NotNull(message = "bookId is required")
        UUID bookId,
        @Min(value = 1, message = "quantity must be at least 1")
        int quantity,
        @NotNull(message = "startDate is required")
        LocalDate startDate,
        @NotNull(message = "dueDate is required")
        LocalDate dueDate
) {
}
