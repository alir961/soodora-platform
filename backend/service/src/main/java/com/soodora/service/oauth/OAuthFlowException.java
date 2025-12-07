package com.soodora.service.oauth;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(value = BAD_REQUEST, reason = "Unable to proceed OAuth 2.0 authorization flow")
public class OAuthFlowException extends RuntimeException {

    public OAuthFlowException(String message) {
        super(message);
    }

}
