package com.grooveconductor.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find user")
public class UnknownUserException extends RuntimeException {

    public UnknownUserException(String message) {
        super(message);
    }

}
