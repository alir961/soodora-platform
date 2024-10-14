package com.grooveconductor.domain.playlist;

import com.grooveconductor.domain.user.UserDTO;

public record PlaylistDTO(
    String slug,
    String name,
    String description,
    PlaylistPrivacy privacy,
    String imageUrl,
    UserDTO owner
) {

}
