package com.champsoft.finalprojectwebservices.borrowing.api;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ReturnRequest(
        @NotNull UUID borrowingTransactionId,
        @NotNull LocalDate returnDate
) {
}
