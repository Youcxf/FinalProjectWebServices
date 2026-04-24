package com.champsoft.catalogservice.shared.presentation;

import com.champsoft.catalogservice.catalog.application.BookNotFoundApplicationException;
import com.champsoft.catalogservice.catalog.domain.DuplicateBookTitleException;
import com.champsoft.catalogservice.catalog.domain.InvalidBookStateException;
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
public class CatalogServiceExceptionHandler {

    @ExceptionHandler(BookNotFoundApplicationException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(BookNotFoundApplicationException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateBookTitleException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(DuplicateBookTitleException exception) {
        return build(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(InvalidBookStateException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessRule(InvalidBookStateException exception) {
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
