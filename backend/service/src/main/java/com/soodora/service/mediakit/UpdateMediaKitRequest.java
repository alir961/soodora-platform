package com.soodora.service.mediakit;

import com.soodora.domain.mediakit.UpdateMediaKitCommand;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateMediaKitRequest(
        @Schema(description = "Title of the media kit", example = "My MediaKit", defaultValue = "My MediaKit") String title,
        @Schema(description = "A short bio", example = "Get all my info !", defaultValue = "Get all my info !") String shortBio,
        @Schema(description = "A long bio that introduce the artist", example = "I'm a House DJ, I'm inspire by Solomun and there is my Soodora MediaKit", defaultValue = "I'm a House DJ, I'm inspire by Solomun and there is my Soodora MediaKit") String longBio,
        @Schema(description = "Email for contact", example = "mail@contact.com", defaultValue = "mail@contact.com") String contactEmail,
        @Schema(description = "Phone number for contact", example = "+3366062636465", defaultValue = "+3366062636465") String contactPhone)
        implements
            Request<UpdateMediaKitCommand> {

    @Override
    public UpdateMediaKitCommand toCommand() {
        return new UpdateMediaKitCommand(
                title,
                shortBio,
                longBio,
                contactEmail,
                contactPhone);
    }
}
