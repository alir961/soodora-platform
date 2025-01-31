package com.grooveconductor.domain.playlist;

import com.grooveconductor.domain.user.UserDTO;

public record PlaylistDTO(
        String slug,
        String name,
        String description,
        int numberOfTracks,
        PlaylistPrivacy privacy,
        String imageUrl,
        UserDTO owner) {

}
