package com.soodora.domain.user;

import java.util.Set;

import static com.soodora.domain.user.IdentifierFactory.createFromSlug;

public record UserDTO(
        String slug,
        String username,
        String email,
        String firstname,
        String lastname,
        Set<String> roles,
        String avatarUrl) {

    public Identifier identifier() {
        return createFromSlug(slug);
    }

}
