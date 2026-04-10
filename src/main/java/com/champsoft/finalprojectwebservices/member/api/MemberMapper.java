package com.champsoft.finalprojectwebservices.member.api;

import com.champsoft.finalprojectwebservices.member.domain.Member;

import java.util.UUID;

public final class MemberMapper {

    private MemberMapper() {
    }

    public static Member toDomain(UUID id, MemberRequest request) {
        return new Member(id, request.name(), request.email(), request.status(), request.outstandingFees());
    }

    public static MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getStatus(),
                member.getOutstandingFees()
        );
    }
}
