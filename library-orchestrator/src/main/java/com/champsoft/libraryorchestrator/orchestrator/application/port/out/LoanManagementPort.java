package com.champsoft.libraryorchestrator.orchestrator.application.port.out;

import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.CreateLoanCommand;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.LoanSnapshot;

import java.util.UUID;

public interface LoanManagementPort {

    LoanSnapshot getLoan(UUID loanId);

    LoanSnapshot createLoan(CreateLoanCommand command);

    LoanSnapshot updateLoan(UUID loanId, CreateLoanCommand command);

    void deleteLoan(UUID loanId);
}
