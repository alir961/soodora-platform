package com.soodora.domain.user;

import com.soodora.domain.auth.value.Role;
import com.soodora.domain.user.value.Identifier;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;

public record PublicUserDTO(
        String slug,
        String username,
        String description,
        String avatarUrl,
        Set<String> roles) {

    public static PublicUserDTO build(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::value)
                .collect(toSet());
        String avatarUrl = user.getAvatarUrl() == null ? null : user.getAvatarUrl().value();

        return new PublicUserDTO(user.getSlug(), user.getUsername().value(), user.getDescription(),
                avatarUrl, roles);
    }

    public Identifier identifier() {
        return createFromSlug(slug);
    }

}
