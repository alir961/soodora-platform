package com.grooveconductor.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find playlist")
public class UnknownPlaylistException extends RuntimeException {

    public UnknownPlaylistException(String message) {
        super(message);
    }

}
