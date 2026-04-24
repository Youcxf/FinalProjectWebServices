package com.champsoft.memberservice.member.application;

public class MemberRequestValidationException extends RuntimeException {

    public MemberRequestValidationException(String message) {
        super(message);
    }
}
