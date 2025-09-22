package com.soodora.service.playlist;

import com.soodora.domain.playlist.PlaylistPrivacy;
import com.soodora.domain.playlist.UpdatePlaylistCommand;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.validatePlaylist;

public record UpdatePlaylistRequest(
        @Schema(description = "Playlist name", example = "My playlist") String name,
        @Schema(description = "Playlist description", example = "My description") String description,
        @Schema(description = "Playlist privacy in the following format: PUBLIC, PRIVATE, PROTECTED", example = "PUBLIC") String privacy)
        implements
            Request<UpdatePlaylistCommand> {

    @Override
    public UpdatePlaylistCommand toCommand() {
        PlaylistPrivacy privacyState = validatePlaylist(name, description, privacy, true);

        return new UpdatePlaylistCommand(name.trim(), description.trim(), privacyState);
    }

}
