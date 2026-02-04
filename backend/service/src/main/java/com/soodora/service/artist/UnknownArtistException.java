package com.soodora.service.artist;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find artist")
public class UnknownArtistException extends RuntimeException {

    public UnknownArtistException(String message) {
        super(message);
    }
}
