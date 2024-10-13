package com.grooveconductor.domain.user;

public record Identifier(IdentifierType type, String value) {

    public enum IdentifierType {
        EMAIL, SLUG
    }

}
