package com.soodora.domain.playlist;

public record UpdatePlaylistCommand(String name, String description, PlaylistPrivacy privacyState) {
}
