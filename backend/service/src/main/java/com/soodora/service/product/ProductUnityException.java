package com.soodora.service.product;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ResponseStatus(value = UNPROCESSABLE_ENTITY, reason = "Violation of the entity unicity")
public class ProductUnityException extends RuntimeException {

    public ProductUnityException(String message) {
        super(message);
    }

}
