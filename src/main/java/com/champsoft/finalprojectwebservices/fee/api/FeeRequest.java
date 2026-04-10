package com.champsoft.finalprojectwebservices.fee.api;

import com.champsoft.finalprojectwebservices.fee.domain.FeeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record FeeRequest(
        @NotNull UUID memberId,
        @NotNull @DecimalMin(value = "0.0") BigDecimal amount,
        @NotNull FeeStatus status
) {
}
