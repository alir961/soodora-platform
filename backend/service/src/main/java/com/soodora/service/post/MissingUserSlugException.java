package com.soodora.service.post;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(value = BAD_REQUEST, reason = "Missing user slug")
public class MissingUserSlugException extends RuntimeException {

    public MissingUserSlugException() {
        super("Missing user slug");
    }

}
