package com.champsoft.libraryorchestrator.orchestrator.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BorrowingDecisionDomainTests {

    @Test
    void createsDecisionResponse() {
        UUID id = UUID.randomUUID();
        BorrowingDecisionResponse response = new BorrowingDecisionResponse(
                id, UUID.randomUUID(), UUID.randomUUID(), 1, LocalDate.now(), LocalDate.now().plusDays(7),
                BorrowingDecisionStatus.APPROVED, "ok", UUID.randomUUID(), Instant.now(), Instant.now()
        );

        assertEquals(id, response.id());
        assertEquals(BorrowingDecisionStatus.APPROVED, response.status());
    }

    @Test
    void conflictExceptionKeepsMessage() {
        BorrowingDecisionConflictException exception = assertThrows(
                BorrowingDecisionConflictException.class,
                () -> {
                    throw new BorrowingDecisionConflictException("bad dates");
                }
        );

        assertEquals("bad dates", exception.getMessage());
    }

    @Test
    void ruleViolationExceptionKeepsMessage() {
        BorrowingRuleViolationException exception = assertThrows(
                BorrowingRuleViolationException.class,
                () -> {
                    throw new BorrowingRuleViolationException("too many copies");
                }
        );

        assertEquals("too many copies", exception.getMessage());
    }
}
