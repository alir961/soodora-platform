package com.soodora.domain.user;

import java.util.Set;

import static com.soodora.domain.user.IdentifierFactory.createFromSlug;

public record PublicUserDTO(
        String slug,
        String username,
        Set<String> roles,
        String avatarUrl) {

    public Identifier identifier() {
        return createFromSlug(slug);
    }

}
