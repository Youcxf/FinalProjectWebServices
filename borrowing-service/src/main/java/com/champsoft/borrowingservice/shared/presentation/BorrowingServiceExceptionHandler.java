package com.champsoft.borrowingservice.shared.presentation;

import com.champsoft.borrowingservice.borrowing.application.LoanNotFoundApplicationException;
import com.champsoft.borrowingservice.borrowing.domain.DuplicateLoanException;
import com.champsoft.borrowingservice.borrowing.domain.InvalidLoanStateException;
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
public class BorrowingServiceExceptionHandler {

    @ExceptionHandler(LoanNotFoundApplicationException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(LoanNotFoundApplicationException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateLoanException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(DuplicateLoanException exception) {
        return build(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(InvalidLoanStateException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessRule(InvalidLoanStateException exception) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
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
