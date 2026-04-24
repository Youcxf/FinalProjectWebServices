package com.champsoft.libraryorchestrator.shared.presentation;

import com.champsoft.libraryorchestrator.orchestrator.application.BorrowingDecisionNotFoundApplicationException;
import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamDependencyException;
import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamResourceNotFoundApplicationException;
import com.champsoft.libraryorchestrator.orchestrator.domain.BorrowingDecisionConflictException;
import com.champsoft.libraryorchestrator.orchestrator.domain.BorrowingRuleViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OrchestratorExceptionHandler {

    @ExceptionHandler(BorrowingDecisionNotFoundApplicationException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(BorrowingDecisionNotFoundApplicationException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DownstreamResourceNotFoundApplicationException.class)
    public ResponseEntity<ApiErrorResponse> handleDownstreamNotFound(DownstreamResourceNotFoundApplicationException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(BorrowingDecisionConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(BorrowingDecisionConflictException exception) {
        return build(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(BorrowingRuleViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleRuleViolation(BorrowingRuleViolationException exception) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(DownstreamDependencyException.class)
    public ResponseEntity<ApiErrorResponse> handleDependency(DownstreamDependencyException exception) {
        return build(HttpStatus.BAD_GATEWAY, exception.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                List.of()
        ));
    }
}
