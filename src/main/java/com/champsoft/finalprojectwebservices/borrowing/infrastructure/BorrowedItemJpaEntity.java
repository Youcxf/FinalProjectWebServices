package com.champsoft.finalprojectwebservices.borrowing.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "borrowed_items")
public class BorrowedItemJpaEntity {

    @Id
    private UUID id;
    private UUID bookId;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrowing_transaction_id")
    private BorrowingJpaEntity borrowingTransaction;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BorrowingJpaEntity getBorrowingTransaction() {
        return borrowingTransaction;
    }

    public void setBorrowingTransaction(BorrowingJpaEntity borrowingTransaction) {
        this.borrowingTransaction = borrowingTransaction;
    }
}
