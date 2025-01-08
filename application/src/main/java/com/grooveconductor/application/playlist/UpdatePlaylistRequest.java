package com.grooveconductor.application.playlist;

import com.grooveconductor.application.common.Request;
import com.grooveconductor.domain.playlist.PlaylistPrivacy;
import com.grooveconductor.domain.playlist.UpdatePlaylistCommand;
import com.grooveconductor.application.common.InvalidRequestException;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.stream.Stream;
import static java.util.stream.Collectors.joining;
import static java.util.Optional.ofNullable;

public record UpdatePlaylistRequest(
    String name,
    String description,
    Integer numberOfTracks,
    @Schema(description =
        "Playlist privacy in the following format: PUBLIC, PRIVATE, PROTECTED",
        example = "PUBLIC")
    String privacy,
    String imageUrl
) implements Request<UpdatePlaylistCommand> {

  static String privacyValues = Stream.of(PlaylistPrivacy.values())
      .map(Enum::toString)
      .collect(joining(", "));

  @Override
  public UpdatePlaylistCommand toCommand() {
    PlaylistPrivacy privacyState = parsePrivacy(privacy);

    return new UpdatePlaylistCommand(
        ofNullable(name),
        ofNullable(description),
        ofNullable(imageUrl),
        ofNullable(numberOfTracks),
        ofNullable(privacyState));
  }

  private PlaylistPrivacy parsePrivacy(String privacy) {
    if (!isValidPrivacyState(privacy)) {
      throw new InvalidRequestException("Privacy is not valid (values: " + privacyValues + ")");
    }
    return PlaylistPrivacy.valueOf(privacy.toUpperCase());
  }

  private boolean isValidPrivacyState(String state) {
    for (PlaylistPrivacy element : PlaylistPrivacy.values()) {
      if (element.name().equalsIgnoreCase(state))
        return true;
    }
    return false;
  }

}
