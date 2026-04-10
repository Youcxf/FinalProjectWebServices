package com.champsoft.finalprojectwebservices.borrowing.domain;

import com.champsoft.finalprojectwebservices.shared.domain.BusinessRuleViolationException;

import java.time.LocalDate;

public record BorrowingPeriod(LocalDate startDate, LocalDate dueDate) {

    public BorrowingPeriod {
        if (startDate == null || dueDate == null) {
            throw new BusinessRuleViolationException("Borrowing period dates are required");
        }
        if (dueDate.isBefore(startDate)) {
            throw new BusinessRuleViolationException("Borrowing due date must be on or after the start date");
        }
    }
}
