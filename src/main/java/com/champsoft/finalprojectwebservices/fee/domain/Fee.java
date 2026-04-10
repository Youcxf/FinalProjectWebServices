package com.champsoft.finalprojectwebservices.fee.domain;

import com.champsoft.finalprojectwebservices.shared.domain.BusinessRuleViolationException;

import java.math.BigDecimal;
import java.util.UUID;

public class Fee {

    private final UUID id;
    private UUID memberId;
    private BigDecimal amount;
    private FeeStatus status;

    public Fee(UUID id, UUID memberId, BigDecimal amount, FeeStatus status) {
        this.id = id;
        this.memberId = memberId;
        this.amount = amount;
        this.status = status == null ? FeeStatus.UNPAID : status;
        validate();
    }

    public void update(UUID memberId, BigDecimal amount, FeeStatus status) {
        this.memberId = memberId;
        this.amount = amount;
        this.status = status == null ? FeeStatus.UNPAID : status;
        validate();
    }

    public void ensureBorrowingAllowed(BigDecimal threshold) {
        if (status == FeeStatus.UNPAID && amount.compareTo(threshold) > 0) {
            throw new BusinessRuleViolationException("Cannot borrow if unpaid fees exceed threshold");
        }
    }

    private void validate() {
        if (memberId == null) {
            throw new BusinessRuleViolationException("Fee must reference a member");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolationException("Fee amount must be zero or positive");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public FeeStatus getStatus() {
        return status;
    }
}
