package com.soodora.domain.user.value;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

import static com.soodora.domain.user.value.Identifier.IdentifierType.*;

public record Identifier(IdentifierType type, String value) {

    public enum IdentifierType {
        EMAIL, SLUG, USERNAME
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class Factory {

        public static Identifier createFromEmail(String email) {
            return new Identifier(EMAIL, email);
        }

        public static Identifier createFromSlug(String slug) {
            return new Identifier(SLUG, slug);
        }

        public static Identifier createFromUsername(String username) {
            return new Identifier(USERNAME, username);
        }

    }

}
