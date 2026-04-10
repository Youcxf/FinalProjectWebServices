package com.champsoft.finalprojectwebservices.member.api;

import com.champsoft.finalprojectwebservices.member.domain.MembershipStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record MemberResponse(
        UUID id,
        String name,
        String email,
        MembershipStatus status,
        BigDecimal outstandingFees
) {
}
