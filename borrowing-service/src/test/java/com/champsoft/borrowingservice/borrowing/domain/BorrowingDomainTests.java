package com.champsoft.borrowingservice.borrowing.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BorrowingDomainTests {

    @Test
    void createsLoanResponse() {
        UUID id = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        LoanResponse response = new LoanResponse(id, memberId, bookId, 1, LocalDate.now(), LocalDate.now().plusDays(7), LoanStatus.ACTIVE);

        assertEquals(id, response.id());
        assertEquals(LoanStatus.ACTIVE, response.status());
    }

    @Test
    void duplicateLoanExceptionKeepsMessage() {
        DuplicateLoanException exception = assertThrows(
                DuplicateLoanException.class,
                () -> {
                    throw new DuplicateLoanException("duplicate");
                }
        );

        assertEquals("duplicate", exception.getMessage());
    }

    @Test
    void invalidLoanStateExceptionKeepsMessage() {
        InvalidLoanStateException exception = assertThrows(
                InvalidLoanStateException.class,
                () -> {
                    throw new InvalidLoanStateException("bad loan");
                }
        );

        assertEquals("bad loan", exception.getMessage());
    }
}
