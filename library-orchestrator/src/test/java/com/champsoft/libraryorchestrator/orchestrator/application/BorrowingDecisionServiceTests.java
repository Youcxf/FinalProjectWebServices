package com.champsoft.libraryorchestrator.orchestrator.application;

import com.champsoft.libraryorchestrator.orchestrator.application.port.out.BookCatalogPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.LoanManagementPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.MemberLookupPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.BookSnapshot;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.CreateLoanCommand;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.LoanSnapshot;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.MemberSnapshot;
import com.champsoft.libraryorchestrator.orchestrator.dataaccess.BorrowingDecisionEntity;
import com.champsoft.libraryorchestrator.orchestrator.dataaccess.BorrowingDecisionRepository;
import com.champsoft.libraryorchestrator.orchestrator.domain.BorrowingDecisionConflictException;
import com.champsoft.libraryorchestrator.orchestrator.domain.BorrowingDecisionResponse;
import com.champsoft.libraryorchestrator.orchestrator.domain.BorrowingDecisionStatus;
import com.champsoft.libraryorchestrator.orchestrator.domain.BorrowingRuleViolationException;
import com.champsoft.libraryorchestrator.orchestrator.domain.EvaluateBorrowingRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowingDecisionServiceTests {

    @Mock
    private BorrowingDecisionRepository repository;
    @Mock
    private MemberLookupPort memberLookupPort;
    @Mock
    private BookCatalogPort bookCatalogPort;
    @Mock
    private LoanManagementPort loanManagementPort;

    @InjectMocks
    private BorrowingDecisionService service;

    @Test
    void approvesBorrowingAndCreatesLoan() {
        UUID loanId = UUID.randomUUID();
        when(memberLookupPort.getMember(any())).thenReturn(member("ACTIVE", BigDecimal.ZERO));
        when(bookCatalogPort.getBook(any())).thenReturn(book("AVAILABLE", 2));
        when(loanManagementPort.createLoan(any(CreateLoanCommand.class))).thenReturn(new LoanSnapshot(loanId, UUID.randomUUID(), UUID.randomUUID(), 1, "ACTIVE"));
        when(repository.save(any(BorrowingDecisionEntity.class))).thenAnswer(call -> call.getArgument(0));

        BorrowingDecisionResponse response = service.evaluate(request());

        assertThat(response.status()).isEqualTo(BorrowingDecisionStatus.APPROVED);
        assertThat(response.loanId()).isEqualTo(loanId);
    }

    @Test
    void rejectsWhenMemberIsNotActive() {
        when(memberLookupPort.getMember(any())).thenReturn(member("SUSPENDED", BigDecimal.TEN));
        when(bookCatalogPort.getBook(any())).thenReturn(book("AVAILABLE", 2));
        when(repository.save(any(BorrowingDecisionEntity.class))).thenAnswer(call -> call.getArgument(0));

        BorrowingDecisionResponse response = service.evaluate(request());

        assertThat(response.status()).isEqualTo(BorrowingDecisionStatus.REJECTED);
        verify(loanManagementPort, never()).createLoan(any());
    }

    @Test
    void throwsWhenRequestedQuantityIsTooHigh() {
        when(memberLookupPort.getMember(any())).thenReturn(member("ACTIVE", BigDecimal.ZERO));
        when(bookCatalogPort.getBook(any())).thenReturn(book("AVAILABLE", 0));

        assertThrows(BorrowingRuleViolationException.class, () -> service.evaluate(request()));
        verify(repository, never()).save(any());
    }

    @Test
    void invalidDatesThrowConflict() {
        EvaluateBorrowingRequest badRequest = new EvaluateBorrowingRequest(
                UUID.randomUUID(), UUID.randomUUID(), 1, LocalDate.now(), LocalDate.now().minusDays(1)
        );

        assertThrows(BorrowingDecisionConflictException.class, () -> service.evaluate(badRequest));
    }

    @Test
    void getsListsUpdatesAndDeletesDecision() {
        UUID id = UUID.randomUUID();
        BorrowingDecisionEntity entity = entity(id);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.findAll()).thenReturn(List.of(entity));
        when(memberLookupPort.getMember(any())).thenReturn(member("SUSPENDED", BigDecimal.TEN));
        when(bookCatalogPort.getBook(any())).thenReturn(book("AVAILABLE", 1));
        when(repository.save(entity)).thenReturn(entity);

        assertThat(service.getDecision(id).id()).isEqualTo(id);
        assertThat(service.getAllDecisions()).hasSize(1);
        assertThat(service.reevaluate(id, request()).status()).isEqualTo(BorrowingDecisionStatus.REJECTED);

        service.deleteDecision(id);

        verify(repository).delete(entity);
    }

    @Test
    void missingDecisionThrowsException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BorrowingDecisionNotFoundApplicationException.class, () -> service.getDecision(id));
    }

    private EvaluateBorrowingRequest request() {
        return new EvaluateBorrowingRequest(UUID.randomUUID(), UUID.randomUUID(), 1, LocalDate.now(), LocalDate.now().plusDays(7));
    }

    private MemberSnapshot member(String status, BigDecimal fees) {
        return new MemberSnapshot(UUID.randomUUID().toString(), "Amy", "amy@test.com", status, fees);
    }

    private BookSnapshot book(String status, int copies) {
        return new BookSnapshot(UUID.randomUUID().toString(), "Book", "Author", status, copies);
    }

    private BorrowingDecisionEntity entity(UUID id) {
        BorrowingDecisionEntity entity = new BorrowingDecisionEntity();
        entity.setId(id);
        entity.setMemberId(UUID.randomUUID());
        entity.setBookId(UUID.randomUUID());
        entity.setQuantity(1);
        entity.setStartDate(LocalDate.now());
        entity.setDueDate(LocalDate.now().plusDays(7));
        entity.setStatus(BorrowingDecisionStatus.APPROVED);
        entity.setDecisionReason("ok");
        entity.setLoanId(null);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }
}
