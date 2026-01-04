package com.soodora.service.user;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@ResponseStatus(value = CONFLICT, reason = "Violation of the entity unicity")
public class UserUnityException extends RuntimeException {

    public UserUnityException(String message) {
        super(message);
    }

}
