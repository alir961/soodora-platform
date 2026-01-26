package com.soodora.service.event;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find event")
public class UnknownEventException extends RuntimeException {

    public UnknownEventException(String message) {
        super(message);
    }
}
