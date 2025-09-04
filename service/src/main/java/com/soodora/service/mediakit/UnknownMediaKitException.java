package com.soodora.service.mediakit;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find media kit")
public class UnknownMediaKitException extends RuntimeException {
    public UnknownMediaKitException(String message) {
        super(message);
    }
}
