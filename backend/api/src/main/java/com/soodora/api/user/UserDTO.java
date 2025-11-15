package com.soodora.api.user;

import com.soodora.domain.auth.value.Role;
import com.soodora.domain.user.User;
import com.soodora.domain.user.value.Identifier;

import java.util.Date;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;

public record UserDTO(
        String slug,
        String username,
        String email,
        String firstname,
        String lastname,
        String description,
        String avatarUrl,
        Date createdAt,
        Date updatedAt,
        Set<String> roles) {

    public static UserDTO build(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::value)
                .collect(toSet());
        String avatarUrl = user.getAvatarUrl() == null ? null : user.getAvatarUrl().value();

        return new UserDTO(user.getSlug(), user.getUsername().value(), user.getEmail().value(), user.getFirstname(),
                user.getLastname(), user.getDescription(), avatarUrl, user.getCreatedAt(),
                user.getUpdatedAt(), roles);
    }

    public Identifier identifier() {
        return createFromSlug(slug);
    }

}
