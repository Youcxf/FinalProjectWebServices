package com.champsoft.finalprojectwebservices.borrowing.api;

import com.champsoft.finalprojectwebservices.borrowing.domain.ReturnStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ReturnResponse(
        UUID id,
        UUID borrowingTransactionId,
        LocalDate returnDate,
        ReturnStatus returnStatus
) {
}
