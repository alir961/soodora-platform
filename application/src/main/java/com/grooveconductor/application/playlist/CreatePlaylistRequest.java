package com.grooveconductor.application.playlist;

import com.grooveconductor.application.common.Request;
import com.grooveconductor.domain.playlist.PlaylistPrivacy;
import com.grooveconductor.domain.playlist.CreatePlaylistCommand;
import com.grooveconductor.application.common.InvalidRequestException;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public record CreatePlaylistRequest(
    String name,
    String description,
    Integer numberOfTracks,
    @Schema(description =
        "Playlist privacy privacy in the following format: PUBLIC, PRIVATE, PROTECTED",
        example = "PUBLIC")
    String privacy,
    String imageUrl
) implements Request<CreatePlaylistCommand> {

    static String privacyValues = Stream.of(PlaylistPrivacy.values())
        .map(Enum::toString)
        .collect(joining(", "));

    @Override
    public CreatePlaylistCommand toCommand() {
        if (name == null) throw new InvalidRequestException("Name is not valid");
        if (description == null) throw new InvalidRequestException("Description is not valid");
        if (privacy == null || !isValidPrivacyState(privacy)) throw new InvalidRequestException("Privacy privacy is not valid (values: " + privacyValues + ")");
        PlaylistPrivacy privacyState = PlaylistPrivacy.valueOf(privacy.toUpperCase());

        return new CreatePlaylistCommand(name, description, numberOfTracks, privacyState, imageUrl);
    }

    private boolean isValidPrivacyState(String state) {
        for (PlaylistPrivacy element : PlaylistPrivacy.values()) {
            if (element.name().equalsIgnoreCase(state))
                return true;
        }
        return false;
    }

}
