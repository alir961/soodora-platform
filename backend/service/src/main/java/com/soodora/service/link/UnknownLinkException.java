package com.soodora.service.link;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find link")
public class UnknownLinkException extends RuntimeException {

    public UnknownLinkException(String message) {
        super(message);
    }

}
