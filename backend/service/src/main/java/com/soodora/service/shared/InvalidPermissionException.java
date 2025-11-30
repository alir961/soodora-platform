package com.soodora.service.shared;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@ResponseStatus(value = FORBIDDEN, reason = "Invalid Permission")
public class InvalidPermissionException extends RuntimeException {

    public InvalidPermissionException(String message) {
        super(message);
    }

}
