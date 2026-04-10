package com.champsoft.finalprojectwebservices.fee.api;

import com.champsoft.finalprojectwebservices.fee.domain.FeeStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record FeeResponse(
        UUID id,
        UUID memberId,
        BigDecimal amount,
        FeeStatus status
) {
}
