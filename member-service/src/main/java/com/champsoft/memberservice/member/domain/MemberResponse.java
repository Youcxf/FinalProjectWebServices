package com.champsoft.memberservice.member.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record MemberResponse(
        UUID id,
        String name,
        String email,
        MemberStatus status,
        BigDecimal outstandingFees
) {
}
