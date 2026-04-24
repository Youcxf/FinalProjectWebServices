package com.champsoft.borrowingservice.borrowing.domain;

import java.time.LocalDate;
import java.util.UUID;

public record LoanResponse(
        UUID id,
        UUID memberId,
        UUID bookId,
        int quantity,
        LocalDate startDate,
        LocalDate dueDate,
        LoanStatus status
) {
}
