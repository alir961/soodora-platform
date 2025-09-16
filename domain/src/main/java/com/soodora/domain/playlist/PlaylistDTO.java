package com.soodora.domain.playlist;

import com.soodora.domain.user.UserDTO;

import java.util.Date;

public record PlaylistDTO(
        String slug,
        String name,
        String description,
        int numberOfTracks,
        PlaylistPrivacy privacy,
        String imageUrl,
        UserDTO owner,
        Date createdAt,
        Date updatedAt) {

}
