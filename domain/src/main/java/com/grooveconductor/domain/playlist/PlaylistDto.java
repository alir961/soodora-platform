package com.grooveconductor.domain.playlist;

public record PlaylistDto(String slug, String name, String description, PlaylistPrivacy state, String imageUrl, String userSlug) {

}
