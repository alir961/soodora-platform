package com.grooveconductor.domain.dto;

import static com.grooveconductor.domain.factory.IdentifierFactory.createFromSlug;

public record UserDTO(String slug, String username, String email, String firstname, String lastname) {

    public Identifier identifier() {
        return createFromSlug(slug);
    }

}
