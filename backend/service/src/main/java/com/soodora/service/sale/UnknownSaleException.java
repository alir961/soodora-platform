package com.soodora.service.sale;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find sale")
public class UnknownSaleException extends RuntimeException {

    public UnknownSaleException(String message) {
        super(message);
    }

}
