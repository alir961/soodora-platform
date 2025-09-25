package com.soodora.service.quote;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ResponseStatus(value = UNPROCESSABLE_ENTITY, reason = "Violation of the entity unicity")
public class QuoteUnityException extends RuntimeException {

    public QuoteUnityException(String message) {
        super(message);
    }

}
