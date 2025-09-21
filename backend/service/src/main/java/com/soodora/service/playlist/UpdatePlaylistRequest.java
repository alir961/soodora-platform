package com.soodora.service.playlist;

import com.soodora.domain.playlist.PlaylistPrivacy;
import com.soodora.domain.playlist.UpdatePlaylistCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import java.util.stream.Stream;

import io.swagger.v3.oas.annotations.media.Schema;

import static java.util.stream.Collectors.joining;

public record UpdatePlaylistRequest(
        @Schema(description = "Playlist name", example = "My playlist") String name,
        @Schema(description = "Playlist description", example = "My description") String description,
        @Schema(description = "Playlist privacy in the following format: PUBLIC, PRIVATE, PROTECTED", example = "PUBLIC") String privacy)
        implements
            Request<UpdatePlaylistCommand> {

    static String privacyValues = Stream.of(PlaylistPrivacy.values())
            .map(Enum::toString)
            .collect(joining(", "));

    @Override
    public UpdatePlaylistCommand toCommand() {
        PlaylistPrivacy privacyState = parsePrivacy(privacy);

        return new UpdatePlaylistCommand(
                name,
                description,
                privacyState);
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
