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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BorrowingDecisionService {

    private final BorrowingDecisionRepository repository;
    private final MemberLookupPort memberLookupPort;
    private final BookCatalogPort bookCatalogPort;
    private final LoanManagementPort loanManagementPort;

    public BorrowingDecisionService(
            BorrowingDecisionRepository repository,
            MemberLookupPort memberLookupPort,
            BookCatalogPort bookCatalogPort,
            LoanManagementPort loanManagementPort
    ) {
        this.repository = repository;
        this.memberLookupPort = memberLookupPort;
        this.bookCatalogPort = bookCatalogPort;
        this.loanManagementPort = loanManagementPort;
    }

    @Transactional(readOnly = true)
    public List<BorrowingDecisionResponse> getAllDecisions() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BorrowingDecisionResponse getDecision(UUID decisionId) {
        return toResponse(getEntity(decisionId));
    }

    @Transactional(readOnly = true)
    public MemberSnapshot getMember(UUID memberId) {
        return memberLookupPort.getMember(memberId);
    }

    @Transactional(readOnly = true)
    public BookSnapshot getBook(UUID bookId) {
        return bookCatalogPort.getBook(bookId);
    }

    @Transactional(readOnly = true)
    public LoanSnapshot getLoan(UUID loanId) {
        return loanManagementPort.getLoan(loanId);
    }

    public BorrowingDecisionResponse evaluate(EvaluateBorrowingRequest request) {
        validateRequest(request);
        DecisionResult result = assess(request);

        BorrowingDecisionEntity entity = new BorrowingDecisionEntity();
        entity.setId(UUID.randomUUID());
        apply(entity, request, result);
        Instant now = Instant.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return toResponse(repository.save(entity));
    }

    public BorrowingDecisionResponse reevaluate(UUID decisionId, EvaluateBorrowingRequest request) {
        validateRequest(request);
        BorrowingDecisionEntity entity = getEntity(decisionId);
        DecisionResult result = assess(request);

        if (entity.getLoanId() != null && result.status() == BorrowingDecisionStatus.REJECTED) {
            loanManagementPort.deleteLoan(entity.getLoanId());
            entity.setLoanId(null);
        }

        apply(entity, request, result);
        entity.setUpdatedAt(Instant.now());
        return toResponse(repository.save(entity));
    }

    public void deleteDecision(UUID decisionId) {
        BorrowingDecisionEntity entity = getEntity(decisionId);
        if (entity.getLoanId() != null) {
            loanManagementPort.deleteLoan(entity.getLoanId());
        }
        repository.delete(entity);
    }

    private BorrowingDecisionEntity getEntity(UUID decisionId) {
        return repository.findById(decisionId)
                .orElseThrow(() -> new BorrowingDecisionNotFoundApplicationException(decisionId));
    }

    private void validateRequest(EvaluateBorrowingRequest request) {
        if (request.dueDate().isBefore(request.startDate())) {
            throw new BorrowingDecisionConflictException("dueDate must be on or after startDate");
        }
    }

    private DecisionResult assess(EvaluateBorrowingRequest request) {
        MemberSnapshot member = memberLookupPort.getMember(request.memberId());
        BookSnapshot book = bookCatalogPort.getBook(request.bookId());

        if (!"ACTIVE".equalsIgnoreCase(member.status())) {
            return DecisionResult.rejected("Member is not active.");
        }

        if (member.outstandingFees().compareTo(BigDecimal.ZERO) > 0) {
            return DecisionResult.rejected("Member has outstanding fees.");
        }

        if (!"AVAILABLE".equalsIgnoreCase(book.status())) {
            return DecisionResult.rejected("Book is not available.");
        }

        if (book.availableCopies() < request.quantity()) {
            throw new BorrowingRuleViolationException("Requested quantity exceeds available copies.");
        }

        CreateLoanCommand command = new CreateLoanCommand(
                request.memberId(),
                request.bookId(),
                request.quantity(),
                request.startDate(),
                request.dueDate()
        );

        LoanSnapshot loan = loanManagementPort.createLoan(command);
        return DecisionResult.approved("Loan approved and created.", loan.id());
    }

    private void apply(BorrowingDecisionEntity entity, EvaluateBorrowingRequest request, DecisionResult result) {
        entity.setMemberId(request.memberId());
        entity.setBookId(request.bookId());
        entity.setQuantity(request.quantity());
        entity.setStartDate(request.startDate());
        entity.setDueDate(request.dueDate());
        entity.setStatus(result.status());
        entity.setDecisionReason(result.reason());
        entity.setLoanId(result.loanId());
    }

    private BorrowingDecisionResponse toResponse(BorrowingDecisionEntity entity) {
        return new BorrowingDecisionResponse(
                entity.getId(),
                entity.getMemberId(),
                entity.getBookId(),
                entity.getQuantity(),
                entity.getStartDate(),
                entity.getDueDate(),
                entity.getStatus(),
                entity.getDecisionReason(),
                entity.getLoanId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private record DecisionResult(BorrowingDecisionStatus status, String reason, UUID loanId) {

        private static DecisionResult approved(String reason, UUID loanId) {
            return new DecisionResult(BorrowingDecisionStatus.APPROVED, reason, loanId);
        }

        private static DecisionResult rejected(String reason) {
            return new DecisionResult(BorrowingDecisionStatus.REJECTED, reason, null);
        }
    }
}
