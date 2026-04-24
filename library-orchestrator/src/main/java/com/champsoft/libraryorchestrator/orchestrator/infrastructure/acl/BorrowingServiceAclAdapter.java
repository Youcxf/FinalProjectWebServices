package com.champsoft.libraryorchestrator.orchestrator.infrastructure.acl;

import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamDependencyException;
import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamResourceNotFoundApplicationException;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.LoanManagementPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.CreateLoanCommand;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.LoanSnapshot;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.UUID;

@Component
public class BorrowingServiceAclAdapter implements LoanManagementPort {

    private final WebClient webClient;

    public BorrowingServiceAclAdapter(@Qualifier("borrowingWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public LoanSnapshot getLoan(UUID loanId) {
        try {
            return webClient.get()
                    .uri("/api/v1/loans/{loanId}", loanId)
                    .retrieve()
                    .bodyToMono(LoanSnapshot.class)
                    .block();
        } catch (WebClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new DownstreamResourceNotFoundApplicationException("Loan", loanId);
            }
            throw new DownstreamDependencyException("Borrowing service error: " + exception.getStatusCode());
        }
    }

    @Override
    public LoanSnapshot createLoan(CreateLoanCommand command) {
        try {
            return webClient.post()
                    .uri("/api/v1/loans")
                    .bodyValue(toBody(command))
                    .retrieve()
                    .bodyToMono(LoanSnapshot.class)
                    .block();
        } catch (WebClientResponseException exception) {
            throw new DownstreamDependencyException("Borrowing service error: " + exception.getStatusCode());
        }
    }

    @Override
    public LoanSnapshot updateLoan(UUID loanId, CreateLoanCommand command) {
        try {
            return webClient.put()
                    .uri("/api/v1/loans/{loanId}", loanId)
                    .bodyValue(toBody(command))
                    .retrieve()
                    .bodyToMono(LoanSnapshot.class)
                    .block();
        } catch (WebClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new DownstreamResourceNotFoundApplicationException("Loan", loanId);
            }
            throw new DownstreamDependencyException("Borrowing service error: " + exception.getStatusCode());
        }
    }

    @Override
    public void deleteLoan(UUID loanId) {
        try {
            webClient.delete()
                    .uri("/api/v1/loans/{loanId}", loanId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException exception) {
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
