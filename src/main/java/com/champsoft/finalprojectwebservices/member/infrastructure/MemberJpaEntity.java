package com.champsoft.finalprojectwebservices.member.infrastructure;

import com.champsoft.finalprojectwebservices.member.domain.MembershipStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "members")
public class MemberJpaEntity {

    @Id
    private UUID id;
    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private MembershipStatus status;
    private BigDecimal outstandingFees;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    public BigDecimal getOutstandingFees() {
        return outstandingFees;
    }

    public void setOutstandingFees(BigDecimal outstandingFees) {
        this.outstandingFees = outstandingFees;
    }
}
