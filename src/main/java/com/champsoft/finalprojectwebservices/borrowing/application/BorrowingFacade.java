package com.champsoft.finalprojectwebservices.borrowing.application;

import com.champsoft.finalprojectwebservices.book.api.BookResponse;
import com.champsoft.finalprojectwebservices.book.application.BookService;
import com.champsoft.finalprojectwebservices.book.domain.Book;
import com.champsoft.finalprojectwebservices.borrowing.api.BorrowingItemRequest;
import com.champsoft.finalprojectwebservices.borrowing.api.BorrowingRequest;
import com.champsoft.finalprojectwebservices.borrowing.api.BorrowingResponse;
import com.champsoft.finalprojectwebservices.borrowing.api.ReturnResponse;
import com.champsoft.finalprojectwebservices.borrowing.domain.*;
import com.champsoft.finalprojectwebservices.fee.application.FeeService;
import com.champsoft.finalprojectwebservices.fee.domain.FeeStatus;
import com.champsoft.finalprojectwebservices.member.api.MemberResponse;
import com.champsoft.finalprojectwebservices.member.application.MemberService;
import com.champsoft.finalprojectwebservices.member.domain.Member;
import com.champsoft.finalprojectwebservices.shared.domain.BusinessRuleViolationException;
import com.champsoft.finalprojectwebservices.shared.domain.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BorrowingFacade {

    private static final BigDecimal UNPAID_FEE_THRESHOLD = BigDecimal.ZERO;

    private final BorrowingRepository borrowingRepository;
    private final ReturnRepository returnRepository;
    private final MemberService memberService;
    private final BookService bookService;
    private final FeeService feeService;

    public BorrowingFacade(BorrowingRepository borrowingRepository, ReturnRepository returnRepository, MemberService memberService, BookService bookService, FeeService feeService) {
        this.borrowingRepository = borrowingRepository;
        this.returnRepository = returnRepository;
        this.memberService = memberService;
        this.bookService = bookService;
        this.feeService = feeService;
    }

    @Transactional(readOnly = true)
    public List<BorrowingResponse> getBorrowingViews() {
        return borrowingRepository.findAll().stream().map(this::buildBorrowingResponse).toList();
    }

    @Transactional(readOnly = true)
    public BorrowingResponse getBorrowingView(UUID id) {
        return buildBorrowingResponse(getBorrowingById(id));
    }

    public BorrowingTransaction createBorrowing(BorrowingRequest request) {
        Member member = memberService.getById(request.memberId());
        member.ensureEligibleToBorrow();
        feeService.getByMemberId(request.memberId()).stream()
                .filter(fee -> fee.getStatus() == FeeStatus.UNPAID)
                .forEach(fee -> fee.ensureBorrowingAllowed(UNPAID_FEE_THRESHOLD));

        List<BorrowedItem> items = toBorrowedItems(request.items());
        items.forEach(item -> {
            Book book = bookService.getById(item.getBookId());
            book.borrowCopies(item.getQuantity());
            bookService.save(book);
        });

        BorrowingTransaction borrowing = new BorrowingTransaction(
                UUID.randomUUID(),
                request.memberId(),
                request.status(),
                new BorrowingPeriod(request.startDate(), request.dueDate()),
                items
        );
        return borrowingRepository.save(borrowing);
    }

    public BorrowingTransaction updateBorrowing(UUID id, BorrowingRequest request) {
        BorrowingTransaction existing = getBorrowingById(id);
        restoreInventory(existing);
        reserveInventory(request.items());
        existing.update(
                request.status(),
                new BorrowingPeriod(request.startDate(), request.dueDate()),
                toBorrowedItems(request.items())
        );
        return borrowingRepository.save(existing);
    }

    public void deleteBorrowing(UUID id) {
        if (!borrowingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Borrowing transaction not found: " + id);
        }
        restoreInventory(getBorrowingById(id));
        borrowingRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ReturnTransaction> getAllReturns() {
        return returnRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ReturnTransaction getReturnById(UUID id) {
        return returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return transaction not found: " + id));
    }

    public ReturnTransaction createReturn(UUID borrowingId, LocalDate returnDate) {
        BorrowingTransaction borrowing = getBorrowingById(borrowingId);
        returnRepository.findByBorrowingTransactionId(borrowingId).ifPresent(existing -> {
            throw new BusinessRuleViolationException("Cannot return the same borrowing twice");
        });

        ReturnStatus returnStatus = returnDate.isAfter(borrowing.getBorrowingPeriod().dueDate())
                ? ReturnStatus.LATE_RETURN
                : ReturnStatus.ON_TIME;

        borrowing.markReturned(returnDate);
        borrowingRepository.save(borrowing);
        borrowing.getBorrowedItems().forEach(item -> {
            Book book = bookService.getById(item.getBookId());
            book.returnCopies(item.getQuantity());
            bookService.save(book);
        });

        return returnRepository.save(new ReturnTransaction(UUID.randomUUID(), borrowingId, returnDate, returnStatus));
    }

    public ReturnTransaction updateReturn(UUID id, UUID borrowingId, LocalDate returnDate) {
        if (!returnRepository.existsById(id)) {
            throw new ResourceNotFoundException("Return transaction not found: " + id);
        }
        BorrowingTransaction borrowing = getBorrowingById(borrowingId);
        ReturnStatus status = returnDate.isAfter(borrowing.getBorrowingPeriod().dueDate())
                ? ReturnStatus.LATE_RETURN
                : ReturnStatus.ON_TIME;
        return returnRepository.save(new ReturnTransaction(id, borrowingId, returnDate, status));
    }

    public void deleteReturn(UUID id) {
        if (!returnRepository.existsById(id)) {
            throw new ResourceNotFoundException("Return transaction not found: " + id);
        }
        returnRepository.deleteById(id);
    }

    public ReturnResponse toReturnResponse(ReturnTransaction returnTransaction) {
        return new ReturnResponse(
                returnTransaction.getId(),
                returnTransaction.getBorrowingTransactionId(),
                returnTransaction.getReturnDate(),
                returnTransaction.getReturnStatus()
        );
    }

    private BorrowingTransaction getBorrowingById(UUID id) {
        return borrowingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing transaction not found: " + id));
    }

    private BorrowingResponse buildBorrowingResponse(BorrowingTransaction borrowing) {
        Member member = memberService.getById(borrowing.getMemberId());
        MemberResponse memberResponse = new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getStatus(),
                member.getOutstandingFees()
        );

        List<BorrowingResponse.BorrowingResponseItem> items = borrowing.getBorrowedItems().stream().map(item -> {
            Book book = bookService.getById(item.getBookId());
            BookResponse bookResponse = new BookResponse(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getStatus(),
                    book.getAvailableCopies()
            );
            return new BorrowingResponse.BorrowingResponseItem(item.getId(), item.getBookId(), item.getQuantity(), bookResponse);
        }).toList();

        BorrowingResponse.ReturnSummary returnSummary = returnRepository.findByBorrowingTransactionId(borrowing.getId())
                .map(returnTx -> new BorrowingResponse.ReturnSummary(returnTx.getId(), returnTx.getReturnDate(), returnTx.getReturnStatus()))
                .orElse(null);

        return new BorrowingResponse(
                borrowing.getId(),
                borrowing.getMemberId(),
                borrowing.getStatus(),
                borrowing.getBorrowingPeriod().startDate(),
                borrowing.getBorrowingPeriod().dueDate(),
                memberResponse,
                items,
                returnSummary
        );
    }

    private List<BorrowedItem> toBorrowedItems(List<BorrowingItemRequest> items) {
        return items.stream()
                .map(item -> new BorrowedItem(UUID.randomUUID(), item.bookId(), item.quantity()))
                .toList();
    }

    private void reserveInventory(List<BorrowingItemRequest> items) {
        items.forEach(item -> {
            Book book = bookService.getById(item.bookId());
            book.borrowCopies(item.quantity());
            bookService.save(book);
        });
    }

    private void restoreInventory(BorrowingTransaction borrowing) {
        borrowing.getBorrowedItems().forEach(item -> {
            Book book = bookService.getById(item.getBookId());
            book.returnCopies(item.getQuantity());
            bookService.save(book);
        });
    }
}
