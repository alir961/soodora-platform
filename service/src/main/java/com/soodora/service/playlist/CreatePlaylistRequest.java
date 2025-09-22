package com.soodora.service.playlist;

import com.soodora.domain.playlist.CreatePlaylistCommand;
import com.soodora.domain.playlist.PlaylistPrivacy;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.validatePlaylist;

public record CreatePlaylistRequest(
        @Schema(description = "Playlist name", example = "My playlist") String name,
        @Schema(description = "Playlist description", example = "My description") String description,
        @Schema(description = "Playlist privacy in the following format: PUBLIC, PRIVATE, UNLISTED", example = "PUBLIC") String privacy)
        implements
            Request<CreatePlaylistCommand> {

    @Override
    public CreatePlaylistCommand toCommand() {
        PlaylistPrivacy privacyState = validatePlaylist(name, description, privacy, false);

        return new CreatePlaylistCommand(name.trim(), description.trim(), privacyState);
    }

}
