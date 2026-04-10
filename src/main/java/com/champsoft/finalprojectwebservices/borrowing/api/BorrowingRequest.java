package com.champsoft.finalprojectwebservices.borrowing.api;

import com.champsoft.finalprojectwebservices.borrowing.domain.BorrowingStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BorrowingRequest(
        @NotNull UUID memberId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate dueDate,
        @NotNull BorrowingStatus status,
        @NotEmpty List<@Valid BorrowingItemRequest> items
) {
}
