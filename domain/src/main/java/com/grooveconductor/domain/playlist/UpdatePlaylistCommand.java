package com.grooveconductor.domain.playlist;

import lombok.Getter;

import java.util.Optional;

@Getter
public class UpdatePlaylistCommand {
    private final String name;
    private final String description;
    private final String imageUrl;
    private final PlaylistPrivacy privacyState;

  public UpdatePlaylistCommand(
      Optional<String> name,
      Optional<String> description,
      Optional<String> imageUrl,
      Optional<PlaylistPrivacy> privacyState
  ) {

    this.name = name.orElse(null);
    this.description = description.orElse(null);
    this.imageUrl = imageUrl.orElse(null);
    this.privacyState = privacyState.orElse(null);
  }
}
