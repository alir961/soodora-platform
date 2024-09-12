package com.grooveconductor.domain.exception;

public class UnknownEmailException extends RuntimeException {

    public UnknownEmailException(String message) {
        super(message);
    }

}
