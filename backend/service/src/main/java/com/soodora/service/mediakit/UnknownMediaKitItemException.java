package com.soodora.service.mediakit;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find media kit item")
public class UnknownMediaKitItemException extends RuntimeException {
    public UnknownMediaKitItemException(String message) {
        super(message);
    }
}
