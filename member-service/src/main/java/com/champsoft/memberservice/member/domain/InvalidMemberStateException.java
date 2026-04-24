package com.champsoft.memberservice.member.domain;

public class InvalidMemberStateException extends RuntimeException {

    public InvalidMemberStateException(String message) {
        super(message);
    }
}
