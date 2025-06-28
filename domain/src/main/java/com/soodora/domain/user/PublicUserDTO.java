package com.soodora.domain.user;

import java.util.Set;

import static com.soodora.domain.user.IdentifierFactory.createFromSlug;

public record PublicUserDTO(
        String slug,
        String username,
        String description,
        String avatarUrl,
        Set<String> roles) {

    public Identifier identifier() {
        return createFromSlug(slug);
    }

}
