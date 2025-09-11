package com.soodora.service.shared;

public class InvalidMultipartRequestException extends RuntimeException {

    public InvalidMultipartRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
