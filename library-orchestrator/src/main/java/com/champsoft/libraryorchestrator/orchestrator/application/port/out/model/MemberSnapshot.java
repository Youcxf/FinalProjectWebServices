package com.champsoft.libraryorchestrator.orchestrator.application.port.out.model;

import java.math.BigDecimal;

public record MemberSnapshot(
        String id,
        String name,
        String email,
        String status,
        BigDecimal outstandingFees
) {
}
