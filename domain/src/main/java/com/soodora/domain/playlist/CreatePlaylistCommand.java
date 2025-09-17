package com.soodora.domain.playlist;

public record CreatePlaylistCommand(String name, String description, PlaylistPrivacy privacy) {

}
