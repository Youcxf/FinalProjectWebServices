package com.champsoft.finalprojectwebservices.borrowing.infrastructure;

import com.champsoft.finalprojectwebservices.borrowing.domain.ReturnStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "return_transactions")
public class ReturnJpaEntity {

    @Id
    private UUID id;
    private UUID borrowingTransactionId;
    private LocalDate returnDate;
    @Enumerated(EnumType.STRING)
    private ReturnStatus returnStatus;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBorrowingTransactionId() {
        return borrowingTransactionId;
    }

    public void setBorrowingTransactionId(UUID borrowingTransactionId) {
        this.borrowingTransactionId = borrowingTransactionId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public ReturnStatus getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(ReturnStatus returnStatus) {
        this.returnStatus = returnStatus;
    }
}
