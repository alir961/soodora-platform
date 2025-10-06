package com.soodora.service.linkpage;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find link page")
public class UnknownLinkPageException extends RuntimeException {

    public UnknownLinkPageException(String message) {
        super(message);
    }

}
