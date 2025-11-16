package com.soodora.service.linkhub;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find link hub")
public class UnknownLinkHubException extends RuntimeException {

    public UnknownLinkHubException(String message) {
        super(message);
    }

}
