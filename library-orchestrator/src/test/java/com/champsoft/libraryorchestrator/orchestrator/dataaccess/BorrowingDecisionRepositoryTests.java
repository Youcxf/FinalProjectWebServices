package com.champsoft.libraryorchestrator.orchestrator.dataaccess;

import com.champsoft.libraryorchestrator.orchestrator.domain.BorrowingDecisionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("testing")
class BorrowingDecisionRepositoryTests {

    @Autowired
    private BorrowingDecisionRepository repository;

    @Test
    void savesFindsChecksAndDeletesDecision() {
        UUID memberId = UUID.randomUUID();
        BorrowingDecisionEntity decision = decision(memberId);
        repository.save(decision);

        assertThat(repository.findById(decision.getId())).isPresent();
        assertThat(repository.findByMemberId(memberId)).hasSize(1);
        assertThat(repository.existsByMemberId(memberId)).isTrue();

        repository.delete(decision);

        assertThat(repository.findById(decision.getId())).isEmpty();
        assertThat(repository.existsByMemberId(memberId)).isFalse();
    }

    private BorrowingDecisionEntity decision(UUID memberId) {
        BorrowingDecisionEntity entity = new BorrowingDecisionEntity();
        entity.setId(UUID.randomUUID());
        entity.setMemberId(memberId);
        entity.setBookId(UUID.randomUUID());
        entity.setQuantity(1);
        entity.setStartDate(LocalDate.now());
        entity.setDueDate(LocalDate.now().plusDays(7));
        entity.setStatus(BorrowingDecisionStatus.APPROVED);
        entity.setDecisionReason("ok");
        entity.setLoanId(UUID.randomUUID());
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }
}
