package com.champsoft.memberservice.member.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpsertMemberRequest(
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        String email,
        @NotNull(message = "status is required")
        MemberStatus status,
        @NotNull(message = "outstandingFees is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "outstandingFees must be non-negative")
        BigDecimal outstandingFees
) {
}
