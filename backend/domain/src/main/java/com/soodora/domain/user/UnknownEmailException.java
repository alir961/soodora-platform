package com.soodora.domain.user;

public class UnknownEmailException extends RuntimeException {

    public UnknownEmailException(String message) {
        super(message);
    }

}
