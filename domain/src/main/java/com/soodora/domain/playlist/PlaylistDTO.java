package com.soodora.domain.playlist;

import com.soodora.domain.user.User;

import java.util.Date;

public record PlaylistDTO(
        String slug,
        String name,
        String description,
        int numberOfTracks,
        PlaylistPrivacy privacy,
        String imageUrl,
        User owner,
        Date createdAt,
        Date updatedAt) {

}
