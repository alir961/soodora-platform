package com.grooveconductor.domain.dto;

import com.grooveconductor.domain.PlaylistPrivacy;

public record PlaylistDto(String slug, String name, String description, PlaylistPrivacy state, String imageUrl, String userSlug) {

}
