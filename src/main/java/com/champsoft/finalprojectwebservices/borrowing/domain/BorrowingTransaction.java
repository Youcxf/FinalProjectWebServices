package com.champsoft.finalprojectwebservices.borrowing.domain;

import com.champsoft.finalprojectwebservices.shared.domain.BusinessRuleViolationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BorrowingTransaction {

    private final UUID id;
    private final UUID memberId;
    private BorrowingStatus status;
    private BorrowingPeriod borrowingPeriod;
    private final List<BorrowedItem> borrowedItems;

    public BorrowingTransaction(UUID id, UUID memberId, BorrowingStatus status, BorrowingPeriod borrowingPeriod, List<BorrowedItem> borrowedItems) {
        if (memberId == null) {
            throw new BusinessRuleViolationException("Borrowing transaction must reference a member");
        }
        if (borrowedItems == null || borrowedItems.isEmpty()) {
            throw new BusinessRuleViolationException("A borrowing transaction must contain at least one borrowed item");
        }
        this.id = id;
        this.memberId = memberId;
        this.status = status == null ? BorrowingStatus.ACTIVE : status;
        this.borrowingPeriod = borrowingPeriod;
        this.borrowedItems = new ArrayList<>(borrowedItems);
    }

    public void update(BorrowingStatus status, BorrowingPeriod borrowingPeriod, List<BorrowedItem> borrowedItems) {
        if (borrowedItems == null || borrowedItems.isEmpty()) {
            throw new BusinessRuleViolationException("A borrowing transaction must contain at least one borrowed item");
        }
        this.status = status == null ? BorrowingStatus.ACTIVE : status;
        this.borrowingPeriod = borrowingPeriod;
        this.borrowedItems.clear();
        this.borrowedItems.addAll(borrowedItems);
    }

    public void markReturned(LocalDate returnDate) {
        status = returnDate.isAfter(borrowingPeriod.dueDate()) ? BorrowingStatus.LATE : BorrowingStatus.RETURNED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public BorrowingStatus getStatus() {
        return status;
    }

    public BorrowingPeriod getBorrowingPeriod() {
        return borrowingPeriod;
    }

    public List<BorrowedItem> getBorrowedItems() {
        return Collections.unmodifiableList(borrowedItems);
    }
}
