package com.champsoft.finalprojectwebservices.borrowing.domain;

import com.champsoft.finalprojectwebservices.shared.domain.BusinessRuleViolationException;

import java.util.UUID;

public class BorrowedItem {

    private final UUID id;
    private final UUID bookId;
    private final int quantity;

    public BorrowedItem(UUID id, UUID bookId, int quantity) {
        if (bookId == null) {
            throw new BusinessRuleViolationException("Borrowed item must reference a book");
        }
        if (quantity <= 0) {
            throw new BusinessRuleViolationException("Borrowed item quantity must be positive");
        }
        this.id = id;
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }
}
