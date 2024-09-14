package com.grooveconductor.domain.factory;

import com.grooveconductor.domain.dto.Identifier;

public class IdentifierFactory {

    private IdentifierFactory() {}

    public static Identifier createFromEmail(String email) {
        return new Identifier(Identifier.IdentifierType.EMAIL, email);
    }

    public static Identifier createFromSlug(String slug) {
        return new Identifier(Identifier.IdentifierType.SLUG, slug);
    }

}
