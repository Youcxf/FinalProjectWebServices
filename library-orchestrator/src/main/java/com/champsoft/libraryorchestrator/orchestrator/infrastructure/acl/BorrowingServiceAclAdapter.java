package com.champsoft.libraryorchestrator.orchestrator.infrastructure.acl;

import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamDependencyException;
import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamResourceNotFoundApplicationException;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.LoanManagementPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.CreateLoanCommand;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.LoanSnapshot;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Component
public class BorrowingServiceAclAdapter implements LoanManagementPort {

    private final RestClient restClient;

    public BorrowingServiceAclAdapter(@Qualifier("borrowingRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public LoanSnapshot getLoan(UUID loanId) {
        try {
            return restClient.get()
                    .uri("/api/v1/loans/{loanId}", loanId)
                    .retrieve()
                    .body(LoanSnapshot.class);
        } catch (HttpStatusCodeException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new DownstreamResourceNotFoundApplicationException("Loan", loanId);
            }
            throw new DownstreamDependencyException("Borrowing service error: " + exception.getStatusCode());
        }
    }

    @Override
    public LoanSnapshot createLoan(CreateLoanCommand command) {
        try {
            return restClient.post()
                    .uri("/api/v1/loans")
                    .body(toBody(command))
                    .retrieve()
                    .body(LoanSnapshot.class);
        } catch (HttpStatusCodeException exception) {
            throw new DownstreamDependencyException("Borrowing service error: " + exception.getStatusCode());
        }
    }

    @Override
    public LoanSnapshot updateLoan(UUID loanId, CreateLoanCommand command) {
        try {
            return restClient.put()
                    .uri("/api/v1/loans/{loanId}", loanId)
                    .body(toBody(command))
                    .retrieve()
                    .body(LoanSnapshot.class);
        } catch (HttpStatusCodeException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new DownstreamResourceNotFoundApplicationException("Loan", loanId);
            }
            throw new DownstreamDependencyException("Borrowing service error: " + exception.getStatusCode());
        }
    }

    @Override
    public void deleteLoan(UUID loanId) {
        try {
            restClient.delete()
                    .uri("/api/v1/loans/{loanId}", loanId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpStatusCodeException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new DownstreamResourceNotFoundApplicationException("Loan", loanId);
            }
            throw new DownstreamDependencyException("Borrowing service error: " + exception.getStatusCode());
        }
    }

    private Map<String, Object> toBody(CreateLoanCommand command) {
        return Map.of(
                "memberId", command.memberId(),
                "bookId", command.bookId(),
                "quantity", command.quantity(),
                "startDate", command.startDate(),
                "dueDate", command.dueDate(),
                "status", "ACTIVE"
        );
    }
}
