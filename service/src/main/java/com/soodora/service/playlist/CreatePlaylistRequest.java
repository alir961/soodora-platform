package com.soodora.service.playlist;

import com.soodora.domain.playlist.CreatePlaylistCommand;
import com.soodora.domain.playlist.PlaylistPrivacy;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import java.util.stream.Stream;

import io.swagger.v3.oas.annotations.media.Schema;

import static java.util.stream.Collectors.joining;

public record CreatePlaylistRequest(
        @Schema(description = "Playlist name", example = "My playlist") String name,
        @Schema(description = "Playlist description", example = "My description") String description,
        @Schema(description = "Playlist privacy privacy in the following format: PUBLIC, PRIVATE, UNLISTED", example = "PUBLIC") String privacy)
        implements
            Request<CreatePlaylistCommand> {

    static String privacyValues = Stream.of(PlaylistPrivacy.values())
            .map(Enum::toString)
            .collect(joining(", "));

    @Override
    public CreatePlaylistCommand toCommand() {
        if (name == null || name.isEmpty())
            throw new InvalidRequestException("Name is not valid");
        if (description == null || description.isEmpty())
            throw new InvalidRequestException("Description is not valid");
        if (privacy == null || !isValidPrivacyState(privacy))
            throw new InvalidRequestException("Privacy privacy is not valid (values: " + privacyValues + ")");
        PlaylistPrivacy privacyState = PlaylistPrivacy.valueOf(privacy.toUpperCase());

        return new CreatePlaylistCommand(name.trim(), description.trim(), privacyState);
    }

    private boolean isValidPrivacyState(String state) {
        for (PlaylistPrivacy element : PlaylistPrivacy.values()) {
            if (element.name().equalsIgnoreCase(state))
                return true;
        }
        return false;
    }

}
