package com.grooveconductor.domain.playlist;

public record CreatePlaylistCommand(String name, String description, PlaylistPrivacy privacy, String imageUrl) {

}
