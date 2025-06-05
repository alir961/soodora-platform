package com.grooveconductor.service.post;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "Unable to find post or comment")
public class UnknownPostCommentException extends RuntimeException {

    public UnknownPostCommentException(String message) {
        super(message);
    }

}
