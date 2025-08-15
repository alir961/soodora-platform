package com.soodora.domain.playlist;

public record UpdatePlaylistCommand(String name, String description, String imageUrl, int numberOfTracks,
        PlaylistPrivacy privacyState) {
}
