package com.grooveconductor.domain.playlist;

import lombok.Getter;

@Getter
public class UpdatePlaylistCommand {
    private final String name;
    private final String description;
    private final int numberOfTracks;
    private final String imageUrl;
    private final PlaylistPrivacy privacyState;

  public UpdatePlaylistCommand(
      String name,
      String description,
      String imageUrl,
      int numberOfTracks,
      PlaylistPrivacy privacyState
  ) {

    this.name = name;
    this.description = description;
    this.numberOfTracks = numberOfTracks;
    this.imageUrl = imageUrl;
    this.privacyState = privacyState;
  }
}
