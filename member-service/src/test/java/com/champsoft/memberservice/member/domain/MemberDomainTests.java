package com.champsoft.memberservice.member.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberDomainTests {

    @Test
    void createsMemberResponse() {
        UUID id = UUID.randomUUID();
        MemberResponse response = new MemberResponse(id, "Amy", "amy@test.com", MemberStatus.ACTIVE, BigDecimal.ZERO);

        assertEquals(id, response.id());
        assertEquals(MemberStatus.ACTIVE, response.status());
    }

    @Test
    void duplicateEmailExceptionKeepsMessage() {
        DuplicateMemberEmailException exception = assertThrows(
                DuplicateMemberEmailException.class,
                () -> {
                    throw new DuplicateMemberEmailException("amy@test.com");
                }
        );

        assertEquals("Member email already exists: amy@test.com", exception.getMessage());
    }

    @Test
    void invalidStateExceptionKeepsMessage() {
        InvalidMemberStateException exception = assertThrows(
                InvalidMemberStateException.class,
                () -> {
                    throw new InvalidMemberStateException("bad state");
                }
        );

        assertEquals("bad state", exception.getMessage());
    }
}
