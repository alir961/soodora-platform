package com.grooveconductor.domain.user;

public class LinkPageNotFoundException extends RuntimeException {
    public LinkPageNotFoundException(String message) {
        super(message);
    }
}
