package com.soodora.service.shared;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(value = BAD_REQUEST, reason = "Unable to read multipart file")
public class InvalidMultipartRequestException extends RuntimeException {

    public InvalidMultipartRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
