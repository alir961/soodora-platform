package com.soodora.domain.user;

public record UpdateUserCommand(
        String username,
        String firstname,
        String lastname,
        String avatarUrl) {
}
