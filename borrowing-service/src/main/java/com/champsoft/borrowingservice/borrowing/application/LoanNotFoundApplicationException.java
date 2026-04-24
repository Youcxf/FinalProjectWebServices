package com.champsoft.borrowingservice.borrowing.application;

import java.util.UUID;

public class LoanNotFoundApplicationException extends RuntimeException {

    public LoanNotFoundApplicationException(UUID loanId) {
        super("Loan not found: " + loanId);
    }
}
