package com.soodora.domain.playlist;

public record CreatePlaylistCommand(String name, String description, int numberOfTracks, PlaylistPrivacy privacy,
        String imageUrl) {

}
