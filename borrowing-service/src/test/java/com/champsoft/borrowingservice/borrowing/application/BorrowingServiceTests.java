package com.champsoft.borrowingservice.borrowing.application;

import com.champsoft.borrowingservice.borrowing.dataaccess.LoanEntity;
import com.champsoft.borrowingservice.borrowing.dataaccess.LoanRepository;
import com.champsoft.borrowingservice.borrowing.domain.DuplicateLoanException;
import com.champsoft.borrowingservice.borrowing.domain.LoanResponse;
import com.champsoft.borrowingservice.borrowing.domain.LoanStatus;
import com.champsoft.borrowingservice.borrowing.domain.UpsertLoanRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTests {

    @Mock
    private LoanRepository repository;

    @InjectMocks
    private BorrowingService service;

    @Test
    void createsLoan() {
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(org.mockito.ArgumentMatchers.any(LoanEntity.class))).thenAnswer(call -> call.getArgument(0));

        LoanResponse response = service.createLoan(request(UUID.randomUUID(), UUID.randomUUID(), LoanStatus.ACTIVE));

        assertThat(response.status()).isEqualTo(LoanStatus.ACTIVE);
    }

    @Test
    void createRejectsDuplicateActiveLoan() {
        UUID memberId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        when(repository.findAll()).thenReturn(List.of(entity(UUID.randomUUID(), memberId, bookId)));

        assertThrows(DuplicateLoanException.class, () -> service.createLoan(request(memberId, bookId, LoanStatus.ACTIVE)));
    }

    @Test
    void getsListsUpdatesAndDeletesLoan() {
        UUID id = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        LoanEntity entity = entity(id, memberId, bookId);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.findAll()).thenReturn(List.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        assertThat(service.getLoanById(id).id()).isEqualTo(id);
        assertThat(service.getAllLoans()).hasSize(1);
        assertThat(service.updateLoan(id, request(memberId, bookId, LoanStatus.RETURNED)).status()).isEqualTo(LoanStatus.RETURNED);

        service.deleteLoan(id);

        verify(repository).delete(entity);
    }

    @Test
    void missingLoanThrowsException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundApplicationException.class, () -> service.getLoanById(id));
    }

    private UpsertLoanRequest request(UUID memberId, UUID bookId, LoanStatus status) {
        return new UpsertLoanRequest(memberId, bookId, 1, LocalDate.now(), LocalDate.now().plusDays(7), status);
    }

    private LoanEntity entity(UUID id, UUID memberId, UUID bookId) {
        LoanEntity entity = new LoanEntity();
        entity.setId(id);
        entity.setMemberId(memberId);
        entity.setBookId(bookId);
        entity.setQuantity(1);
        entity.setStartDate(LocalDate.now());
        entity.setDueDate(LocalDate.now().plusDays(7));
        entity.setStatus(LoanStatus.ACTIVE);
        return entity;
    }
}
