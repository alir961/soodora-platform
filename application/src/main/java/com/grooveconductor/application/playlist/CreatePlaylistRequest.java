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
    @Schema(description =
        "Playlist privacy state in the following format: PUBLIC, PRIVATE, PROTECTED",
        example = "PUBLIC")
    String state,
    String imageUrl)
    implements Request<CreatePlaylistCommand> {

    static String privacyValues = Stream.of(PlaylistPrivacy.values())
        .map(Enum::toString)
        .collect(joining(", "));

    @Override
    public CreatePlaylistCommand toCommand() {
        if (name == null) throw new InvalidRequestException("Name is not valid");
        if (description == null) throw new InvalidRequestException("Description is not valid");
        if (state == null || !isValidPrivacyState(state)) throw new InvalidRequestException("Privacy state is not valid (values: " + privacyValues + ")");
        PlaylistPrivacy privacyState = PlaylistPrivacy.valueOf(state.toUpperCase());

        return new CreatePlaylistCommand(name, description, privacyState, imageUrl);
    }

    private boolean isValidPrivacyState(String state) {
        for (PlaylistPrivacy privacy : PlaylistPrivacy.values()) {
            if (privacy.name().equalsIgnoreCase(state))
                return true;
        }
        return false;
    }

}
