package com.soodora.service.quote;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find Quote")
public class UnknownQuoteException extends RuntimeException {

    public UnknownQuoteException(String message) {
        super(message);
    }

}
