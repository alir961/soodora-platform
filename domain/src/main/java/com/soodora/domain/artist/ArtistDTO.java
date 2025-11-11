package com.soodora.domain.artist;

import com.soodora.domain.user.User;

public record ArtistDTO(
        String slug,
        String username,
        String description,
        String avatarUrl) {

    public static ArtistDTO build(User user) {
        return new ArtistDTO(
                user.getSlug(),
                user.getUsername().value(),
                user.getDescription(),
                user.getAvatarUrl() == null ? null : user.getAvatarUrl().value());
    }

}
