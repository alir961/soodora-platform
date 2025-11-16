package com.soodora.service.user;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ResponseStatus(value = UNPROCESSABLE_ENTITY, reason = "Violation of the entity unicity")
public class UserUnityException extends RuntimeException {

    public UserUnityException(String message) {
        super(message);
    }

}
