package com.soodora.service.quote;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find Quote Article")
public class UnknownQuoteArticleException extends RuntimeException {

    public UnknownQuoteArticleException(String message) {
        super(message);
    }

}
