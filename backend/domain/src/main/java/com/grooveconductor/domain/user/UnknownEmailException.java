package com.grooveconductor.domain.user;

public class UnknownEmailException extends RuntimeException {

    public UnknownEmailException(String message) {
        super(message);
    }

}
