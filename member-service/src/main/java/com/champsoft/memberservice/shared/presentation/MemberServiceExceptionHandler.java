package com.champsoft.memberservice.shared.presentation;

import com.champsoft.memberservice.member.application.MemberNotFoundApplicationException;
import com.champsoft.memberservice.member.domain.DuplicateMemberEmailException;
import com.champsoft.memberservice.member.domain.InvalidMemberStateException;
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
public class MemberServiceExceptionHandler {

    @ExceptionHandler(MemberNotFoundApplicationException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(MemberNotFoundApplicationException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateMemberEmailException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(DuplicateMemberEmailException exception) {
        return build(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(InvalidMemberStateException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessRule(InvalidMemberStateException exception) {
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
