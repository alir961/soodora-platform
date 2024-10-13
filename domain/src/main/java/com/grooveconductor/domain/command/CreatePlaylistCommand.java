package com.grooveconductor.domain.command;

import com.grooveconductor.domain.PlaylistPrivacy;

public record CreatePlaylistCommand(String name, String description, PlaylistPrivacy state, String imageUrl) {

}
