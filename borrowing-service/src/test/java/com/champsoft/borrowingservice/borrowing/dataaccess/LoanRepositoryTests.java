package com.champsoft.borrowingservice.borrowing.dataaccess;

import com.champsoft.borrowingservice.borrowing.domain.LoanStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("testing")
class LoanRepositoryTests {

    @Autowired
    private LoanRepository repository;

    @Test
    void savesFindsChecksAndDeletesLoan() {
        UUID memberId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        LoanEntity loan = loan(memberId, bookId);
        repository.save(loan);

        assertThat(repository.findById(loan.getId())).isPresent();
        assertThat(repository.findByMemberId(memberId)).hasSize(1);
        assertThat(repository.existsByMemberIdAndBookIdAndStatus(memberId, bookId, LoanStatus.ACTIVE)).isTrue();

        repository.delete(loan);

        assertThat(repository.findById(loan.getId())).isEmpty();
        assertThat(repository.existsByMemberIdAndBookIdAndStatus(memberId, bookId, LoanStatus.ACTIVE)).isFalse();
    }

    private LoanEntity loan(UUID memberId, UUID bookId) {
        LoanEntity loan = new LoanEntity();
        loan.setId(UUID.randomUUID());
        loan.setMemberId(memberId);
        loan.setBookId(bookId);
        loan.setQuantity(1);
        loan.setStartDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(7));
        loan.setStatus(LoanStatus.ACTIVE);
        return loan;
    }
}
