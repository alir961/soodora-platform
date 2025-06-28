package com.soodora.domain.user;

import java.util.Set;

import static com.soodora.domain.user.IdentifierFactory.createFromSlug;

public record UserDTO(
        String slug,
        String username,
        String email,
        String firstname,
        String lastname,
        String description,
        String avatarUrl,
        Set<String> roles) {

    public Identifier identifier() {
        return createFromSlug(slug);
    }

    public PublicUserDTO toPublic() {
        return new PublicUserDTO(slug, username, description, avatarUrl, roles);
    }

}
