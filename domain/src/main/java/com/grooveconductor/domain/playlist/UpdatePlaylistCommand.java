package com.grooveconductor.domain.playlist;

public record UpdatePlaylistCommand(String name, String description, String imageUrl, PlaylistPrivacy privacyState) {

}
