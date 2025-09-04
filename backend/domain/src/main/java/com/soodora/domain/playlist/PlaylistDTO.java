package com.soodora.domain.playlist;

import com.soodora.domain.user.UserDTO;

public record PlaylistDTO(
        String slug,
        String name,
        String description,
        int numberOfTracks,
        PlaylistPrivacy privacy,
        String imageUrl,
        UserDTO owner) {

}
