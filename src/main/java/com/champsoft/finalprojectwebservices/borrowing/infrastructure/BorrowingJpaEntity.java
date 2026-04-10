package com.champsoft.finalprojectwebservices.borrowing.infrastructure;

import com.champsoft.finalprojectwebservices.borrowing.domain.BorrowingStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "borrowing_transactions")
public class BorrowingJpaEntity {

    @Id
    private UUID id;
    private UUID memberId;
    @Enumerated(EnumType.STRING)
    private BorrowingStatus status;
    private LocalDate startDate;
    private LocalDate dueDate;

    @OneToMany(mappedBy = "borrowingTransaction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BorrowedItemJpaEntity> items = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public BorrowingStatus getStatus() {
        return status;
    }

    public void setStatus(BorrowingStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<BorrowedItemJpaEntity> getItems() {
        return items;
    }

    public void setItems(List<BorrowedItemJpaEntity> items) {
        this.items = items;
    }
}
