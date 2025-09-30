package com.soodora.service.playlist;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ResponseStatus(value = UNPROCESSABLE_ENTITY, reason = "Violation of the entity unicity")
public class PlaylistUnityException extends RuntimeException {

    public PlaylistUnityException(String message) {
        super(message);
    }

}
