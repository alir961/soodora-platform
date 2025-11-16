package com.soodora.service.link;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find playlist")
public class LinkUnityException extends RuntimeException {

    public LinkUnityException(String message) {
        super(message);
    }

}
