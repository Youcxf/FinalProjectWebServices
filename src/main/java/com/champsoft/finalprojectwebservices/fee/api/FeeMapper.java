package com.champsoft.finalprojectwebservices.fee.api;

import com.champsoft.finalprojectwebservices.fee.domain.Fee;

import java.util.UUID;

public final class FeeMapper {

    private FeeMapper() {
    }

    public static Fee toDomain(UUID id, FeeRequest request) {
        return new Fee(id, request.memberId(), request.amount(), request.status());
    }

    public static FeeResponse toResponse(Fee fee) {
        return new FeeResponse(fee.getId(), fee.getMemberId(), fee.getAmount(), fee.getStatus());
    }
}
