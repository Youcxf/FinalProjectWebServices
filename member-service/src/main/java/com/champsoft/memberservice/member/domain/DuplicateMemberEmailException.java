package com.champsoft.memberservice.member.domain;

public class DuplicateMemberEmailException extends RuntimeException {

    public DuplicateMemberEmailException(String email) {
        super("Member email already exists: " + email);
    }
}
