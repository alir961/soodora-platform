package com.grooveconductor.domain.user;

import static com.grooveconductor.domain.user.Identifier.IdentifierType.EMAIL;
import static com.grooveconductor.domain.user.Identifier.IdentifierType.SLUG;

public class IdentifierFactory {

    private IdentifierFactory() {}

    public static Identifier createFromEmail(String email) {
        return new Identifier(EMAIL, email);
    }

    public static Identifier createFromSlug(String slug) {
        return new Identifier(SLUG, slug);
    }

}
