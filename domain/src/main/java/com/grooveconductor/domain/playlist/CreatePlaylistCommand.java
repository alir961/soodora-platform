package com.grooveconductor.domain.playlist;

public record CreatePlaylistCommand(String name, String description, PlaylistPrivacy state, String imageUrl) {

}
