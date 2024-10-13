package com.grooveconductor.domain.user;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;

public record UserDTO(String slug, String username, String email, String firstname, String lastname) {

    public Identifier identifier() {
        return createFromSlug(slug);
    }

}
