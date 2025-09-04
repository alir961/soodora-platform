package com.soodora.service.post;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find post")
public class UnknownPostException extends RuntimeException {

    public UnknownPostException(String message) {
        super(message);
    }

}
