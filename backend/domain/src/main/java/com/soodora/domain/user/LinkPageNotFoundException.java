package com.soodora.domain.user;

public class LinkPageNotFoundException extends RuntimeException {
    public LinkPageNotFoundException(String message) {
        super(message);
    }
}
