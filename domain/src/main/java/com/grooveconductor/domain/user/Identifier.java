package com.grooveconductor.domain.user;

import java.util.Objects;

public record Identifier(IdentifierType type, String value) {

    public enum IdentifierType {
        EMAIL, SLUG
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;

        return Objects.equals(value, that.value) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
