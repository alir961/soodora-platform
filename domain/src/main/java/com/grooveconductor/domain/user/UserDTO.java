package com.grooveconductor.domain.user;

import java.util.Set;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;

public record UserDTO(
    String slug,
    String username,
    String email,
    String firstname,
    String lastname,
    Set<String> roles
) {

    public Identifier identifier() {
        return createFromSlug(slug);
    }

}
