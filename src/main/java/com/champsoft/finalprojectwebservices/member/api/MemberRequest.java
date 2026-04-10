package com.champsoft.finalprojectwebservices.member.api;

import com.champsoft.finalprojectwebservices.member.domain.MembershipStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MemberRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull MembershipStatus status,
        @NotNull @DecimalMin(value = "0.0") BigDecimal outstandingFees
) {
}
