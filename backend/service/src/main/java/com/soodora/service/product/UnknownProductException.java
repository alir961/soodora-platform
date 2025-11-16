package com.soodora.service.product;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find product")
public class UnknownProductException extends RuntimeException {

    public UnknownProductException(String message) {
        super(message);
    }

}
