package com.grooveconductor.domain.user;

import static com.grooveconductor.domain.user.Identifier.IdentifierType.*;

public class IdentifierFactory {

    private IdentifierFactory() {
    }

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
