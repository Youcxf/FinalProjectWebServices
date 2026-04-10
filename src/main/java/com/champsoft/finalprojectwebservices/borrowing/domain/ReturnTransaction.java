package com.champsoft.finalprojectwebservices.borrowing.domain;

import com.champsoft.finalprojectwebservices.shared.domain.BusinessRuleViolationException;

import java.time.LocalDate;
import java.util.UUID;

public class ReturnTransaction {

    private final UUID id;
    private final UUID borrowingTransactionId;
    private final LocalDate returnDate;
    private final ReturnStatus returnStatus;

    public ReturnTransaction(UUID id, UUID borrowingTransactionId, LocalDate returnDate, ReturnStatus returnStatus) {
        if (borrowingTransactionId == null) {
            throw new BusinessRuleViolationException("Return transaction must reference a borrowing transaction");
        }
        if (returnDate == null) {
            throw new BusinessRuleViolationException("Return date is required");
        }
        this.id = id;
        this.borrowingTransactionId = borrowingTransactionId;
        this.returnDate = returnDate;
        this.returnStatus = returnStatus;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBorrowingTransactionId() {
        return borrowingTransactionId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public ReturnStatus getReturnStatus() {
        return returnStatus;
    }
}
