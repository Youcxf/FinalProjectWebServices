package com.champsoft.borrowingservice.borrowing.application;

import com.champsoft.borrowingservice.borrowing.dataaccess.LoanEntity;
import com.champsoft.borrowingservice.borrowing.dataaccess.LoanRepository;
import com.champsoft.borrowingservice.borrowing.domain.DuplicateLoanException;
import com.champsoft.borrowingservice.borrowing.domain.InvalidLoanStateException;
import com.champsoft.borrowingservice.borrowing.domain.LoanResponse;
import com.champsoft.borrowingservice.borrowing.domain.LoanStatus;
import com.champsoft.borrowingservice.borrowing.domain.UpsertLoanRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BorrowingService {

    private final LoanRepository loanRepository;

    public BorrowingService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LoanResponse getLoanById(UUID loanId) {
        return toResponse(getEntity(loanId));
    }

    public LoanResponse createLoan(UpsertLoanRequest request) {
        validateRequest(request);
        boolean duplicateActiveLoan = loanRepository.findAll().stream()
                .anyMatch(existing -> existing.getMemberId().equals(request.memberId())
                        && existing.getBookId().equals(request.bookId())
                        && existing.getStatus() == LoanStatus.ACTIVE);
        if (duplicateActiveLoan) {
            throw new DuplicateLoanException("An active loan already exists for this member and book.");
        }

        LoanEntity entity = new LoanEntity();
        entity.setId(UUID.randomUUID());
        apply(entity, request);
        return toResponse(loanRepository.save(entity));
    }

    public LoanResponse updateLoan(UUID loanId, UpsertLoanRequest request) {
        validateRequest(request);
        LoanEntity entity = getEntity(loanId);
        apply(entity, request);
        return toResponse(loanRepository.save(entity));
    }

    public void deleteLoan(UUID loanId) {
        loanRepository.delete(getEntity(loanId));
    }

    private LoanEntity getEntity(UUID loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundApplicationException(loanId));
    }

    private void validateRequest(UpsertLoanRequest request) {
        if (request.dueDate().isBefore(request.startDate())) {
            throw new BorrowingRequestValidationException("dueDate must be on or after startDate");
        }

        if (request.status() == LoanStatus.RETURNED && request.dueDate().isAfter(request.startDate().plusMonths(2))) {
            throw new InvalidLoanStateException("Returned loans must stay within the standard borrowing window.");
        }
    }

    private void apply(LoanEntity entity, UpsertLoanRequest request) {
        entity.setMemberId(request.memberId());
        entity.setBookId(request.bookId());
        entity.setQuantity(request.quantity());
        entity.setStartDate(request.startDate());
        entity.setDueDate(request.dueDate());
        entity.setStatus(request.status());
    }

    private LoanResponse toResponse(LoanEntity entity) {
        return new LoanResponse(
                entity.getId(),
                entity.getMemberId(),
                entity.getBookId(),
                entity.getQuantity(),
                entity.getStartDate(),
                entity.getDueDate(),
                entity.getStatus()
        );
    }
}
