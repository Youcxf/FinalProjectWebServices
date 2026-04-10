package com.champsoft.finalprojectwebservices.member.domain;

import com.champsoft.finalprojectwebservices.shared.domain.BusinessRuleViolationException;

import java.math.BigDecimal;
import java.util.UUID;

public class Member {

    private final UUID id;
    private String name;
    private String email;
    private MembershipStatus status;
    private BigDecimal outstandingFees;

    public Member(UUID id, String name, String email, MembershipStatus status, BigDecimal outstandingFees) {
        this.id = id;
        this.name = requireText(name, "Member name is required");
        this.email = requireText(email, "Member email is required");
        this.status = status == null ? MembershipStatus.ACTIVE : status;
        this.outstandingFees = outstandingFees == null ? BigDecimal.ZERO : outstandingFees;
        validateOutstandingFees(this.outstandingFees);
    }

    public void update(String name, String email, MembershipStatus status, BigDecimal outstandingFees) {
        this.name = requireText(name, "Member name is required");
        this.email = requireText(email, "Member email is required");
        this.status = status == null ? MembershipStatus.ACTIVE : status;
        this.outstandingFees = outstandingFees == null ? BigDecimal.ZERO : outstandingFees;
        validateOutstandingFees(this.outstandingFees);
    }

    public void ensureEligibleToBorrow() {
        if (status != MembershipStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Suspended members cannot borrow");
        }
        if (outstandingFees.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessRuleViolationException("Members with unpaid fees cannot borrow");
        }
    }

    private void validateOutstandingFees(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolationException("Outstanding fees cannot be negative");
        }
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleViolationException(message);
        }
        return value.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public BigDecimal getOutstandingFees() {
        return outstandingFees;
    }
}
