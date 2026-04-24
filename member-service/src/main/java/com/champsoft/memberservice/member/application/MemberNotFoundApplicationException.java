package com.champsoft.memberservice.member.application;

import java.util.UUID;

public class MemberNotFoundApplicationException extends RuntimeException {

    public MemberNotFoundApplicationException(UUID memberId) {
        super("Member not found: " + memberId);
    }
}
