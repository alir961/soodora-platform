package com.soodora.service.common;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ResponseStatus(value = INTERNAL_SERVER_ERROR, reason = "An exception occurred while generating a document")
public class DocumentGenerationException extends RuntimeException {

    public DocumentGenerationException(String message) {
        super(message);
    }

}
