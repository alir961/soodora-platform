package com.grooveconductor.domain.dto;

public record Identifier(IdentifierType type, String value) {

    public enum IdentifierType {
        EMAIL, SLUG
    }

}
